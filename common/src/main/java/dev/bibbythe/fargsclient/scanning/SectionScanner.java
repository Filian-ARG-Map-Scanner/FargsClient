package dev.bibbythe.fargsclient.scanning;

import dev.bibbythe.fargsclient.types.FoundBlock;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkSection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

public class SectionScanner implements Callable<List<FoundBlock>> {
    private final ChunkSection chunkSection;
    private final ChunkPos chunkPos;
    private final int sectionPos;
    private final String dimension;

    public SectionScanner(ChunkSection chunkSection, ChunkPos chunkPos, int sectionPos, String dimension) {
        this.chunkSection = chunkSection;
        this.chunkPos = chunkPos;
        this.sectionPos = sectionPos;
        this.dimension = dimension;
    }




    @Override
    public List<FoundBlock> call() {
        Predicate<BlockState> blockStatePredicate = blockState ->  (!ScanManager.ignoredBlocks.get(dimension).contains(blockState.getBlock()));
        List<FoundBlock> foundBlocks = new ArrayList<>();
        if (chunkSection.hasAny(blockStatePredicate)) {
            for(int j = 0; j < 16; ++j) {
                for(int k = 0; k < 16; ++k) {
                    for(int l = 0; l < 16; ++l) {
                        BlockState blockState = chunkSection.getBlockState(l, j, k);
                        if (blockStatePredicate.test(blockState)) {
                            BlockPos blockPos =  getBlockPosFromSection(chunkPos, sectionPos, l, j, k);
                            foundBlocks.add(new FoundBlock(Registries.BLOCK.getId(blockState.getBlock()).toString(), blockPos));
                        }
                    }
                }
            }
        }
        return foundBlocks;
    }

    public BlockPos getBlockPosFromSection(ChunkPos chunkPos, int sectionPos, int blockX, int blockY, int blockZ) {
        return new BlockPos(chunkPos.getStartX() + blockX, sectionPos * 16 + blockY, chunkPos.getStartZ() + blockZ);
    }
}
