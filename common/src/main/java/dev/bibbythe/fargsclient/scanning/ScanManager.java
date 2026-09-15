package dev.bibbythe.fargsclient.scanning;

import dev.bibbythe.fargsclient.FargsClient;
import dev.bibbythe.fargsclient.traversal.Autopilot;
import dev.bibbythe.fargsclient.types.FoundBlock;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class ScanManager {
    public static CopyOnWriteArrayList<Block> ignoredBlocks = new CopyOnWriteArrayList<>();
    public static ConcurrentHashMap<Block, String> blockIdCache = new ConcurrentHashMap<>();
    public static boolean scanEnabled = false;
    public static boolean scanRunning = false;
    private static int failedChunkAttempts = 0;

    private static final List<Future<List<FoundBlock>>> sectionScannerFutures = new ArrayList<>();

    private static final ExecutorService sectionScannerService = Executors.newCachedThreadPool();


    private static final ArrayDeque<ChunkPos> failedChunks = new ArrayDeque<>();


    private static boolean firstPassDone = false;


    public static void scanManagerRun(MinecraftClient client) {
        if (Autopilot.movingToTarget)
            return;


        if (scanRunning) {
            if (!sectionScannerFutures.isEmpty() && sectionScannerFutures.stream().allMatch(Future::isDone)) {
                List<FoundBlock> foundBlocks = sectionScannerFutures.stream().map(Future::resultNow).flatMap(List::stream).toList();
                sectionScannerFutures.clear();
                //TODO: send found blocks to server
                FargsClient.LOGGER.info("Found blocks: " + foundBlocks.size());
                scanRunning = false;
            } else {
                if (firstPassDone && !failedChunks.isEmpty()) {
                    failedChunkAttempts++;
                    if (failedChunkAttempts <= 10) {
                        processFailedChunks(client);
                        return;
                    }
                    FargsClient.LOGGER.info("Failed to scan the following chunks after 10 attempts: " + Arrays.toString(failedChunks.stream().map(chunkPos -> chunkPos.x + "" + chunkPos.z).toArray()));
                    failedChunks.clear();
                }
                failedChunkAttempts = 0;
            }
        } else {
            scanRunning = true;
            scanArea(client);
        }
    }

    private static void processFailedChunks(MinecraftClient client) {
        if (client.world == null)
            throw new IllegalStateException("Client world is null");
        for (ChunkPos failedChunk : failedChunks) {
            failedChunks.remove(failedChunk);
            processChunk(client.world, failedChunk.x, failedChunk.z);
        }
    }

    private static void scanArea(MinecraftClient client) {
        if (client == null || client.world == null || client.player == null) {
            throw new IllegalStateException("Client is not ready");
        }

        ChunkPos playerChunkPos = client.player.getChunkPos();
        int scanRadius = Autopilot.hilbertCurve.scanWidth / 2;

        for (int chunkX = playerChunkPos.x - scanRadius; chunkX == playerChunkPos.x + scanRadius - 1; chunkX++) {
            for (int chunkZ = playerChunkPos.z - scanRadius; chunkZ <= playerChunkPos.z + scanRadius - 1; chunkZ++) {
                processChunk(client.world, chunkX, chunkZ);
            }
        }
        firstPassDone = true;
    }

    private static void processChunk(ClientWorld world, int chunkX, int chunkZ) {
        try {
            Chunk chunk = world.getChunkManager().getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);

            if (chunk == null) {
                failedChunks.add(new ChunkPos(chunkX, chunkZ));
                return;
            }
            if (chunk.getStatus() == ChunkStatus.FULL) {
                for (int i = chunk.getBottomSectionCoord(); i <= chunk.getTopSectionCoord(); ++i) {
                    ChunkSection chunkSection = chunk.getSection(chunk.sectionCoordToIndex(i)).copy();
                    if (chunkSection.isEmpty())
                        continue;
                    int sectionPos = chunk.sectionIndexToCoord(i);
                    sectionScannerFutures.add(sectionScannerService.submit(new SectionScanner(chunkSection, chunk.getPos(), sectionPos)));
                }
            }
        } catch (
                IllegalArgumentException e) {
            FargsClient.LOGGER.error("Scan Error", e);
        }
    }



}
