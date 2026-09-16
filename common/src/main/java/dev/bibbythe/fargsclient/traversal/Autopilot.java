package dev.bibbythe.fargsclient.traversal;

import dev.bibbythe.fargsclient.FargsClient;
import dev.bibbythe.fargsclient.events.AutopilotEvents;
import dev.bibbythe.fargsclient.types.Waypoint;
import dev.bibbythe.fargsclient.types.Point;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;

import static dev.bibbythe.fargsclient.scanning.ScanManager.scanRunning;

/**
 * Controller class managing autopilot traversal and player movement along paths and targets.
 */
public class Autopilot {

    /**
     * The current target location the autopilot is navigating toward.
     */
    private static Point targetLocation;
    /**
     * The maximum movement speed (distance per tick) when traveling towards a target.
     */
    private static double maxSpeed = 8D;
    /**
     * Master arm flag enabling or disabling autopilot execution.
     */
    public static boolean autoPilotMasterArm = false;
    /**
     * Flag indicating whether a Hilbert curve traversal pattern is currently running.
     */
    private static boolean runningHilbert = false;
    /**
     * Flag indicating whether the player is currently in transit to a target point.
     */
    public static boolean movingToTarget = false;
    /**
     * The active Hilbert curve path generator instance.
     */
    public static HilbertCurve hilbertCurve;

    /**
     * Enables the autopilot master switch and triggers the {@link AutopilotEvents.EnabledEvent#onEnabled()} event.
     */
    public static void enable() {
        autoPilotMasterArm = true;
        AutopilotEvents.ENABLED.invoker().onEnabled();
    }

    public static void pause() {
        autoPilotMasterArm = false;
        AutopilotEvents.DISABLED.invoker().onDisabled();
    }

    /**
     * Disables the autopilot master switch and triggers the {@link AutopilotEvents.DisabledEvent#onDisabled()} event.
     */
    public static void disable() {
        autoPilotMasterArm = false;
        hilbertCurve = null;
        AutopilotEvents.DISABLED.invoker().onDisabled();
    }

    /**
     * Starts a Hilbert curve traversal starting at a specific chunk position.
     *
     * @param searchWidth the region width (must be a power of 2)
     * @param startPos    the starting chunk coordinates
     * @throws IllegalStateException if the Minecraft player instance is null
     */
    public static void startHilbert(int searchWidth, ChunkPos startPos) throws IllegalStateException {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            throw new IllegalStateException("Player is null");
        }
        hilbertCurve = new HilbertCurve(searchWidth, startPos);
        runningHilbert = true;
        AutopilotEvents.HILBERT_STARTED.invoker().onHilbertStarted(searchWidth, startPos);
    }

    public static void startHilbert(int searchWidth, ChunkPos startPos, int index) throws IllegalStateException {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            throw new IllegalStateException("Player is null");
        }
        hilbertCurve = new HilbertCurve(searchWidth, startPos, index);
        runningHilbert = true;
        AutopilotEvents.HILBERT_STARTED.invoker().onHilbertStarted(searchWidth, startPos);
    }

    /**
     * Starts a Hilbert curve traversal starting at the player's current chunk position.
     *
     * @param searchWidth the region width (must be a power of 2)
     * @throws IllegalStateException if the Minecraft player instance is null
     */
    public static void startHilbert(int searchWidth) throws IllegalStateException {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            throw new IllegalStateException("Player is null");
        }
        hilbertCurve = new HilbertCurve(searchWidth, client.player.getChunkPos());
        runningHilbert = true;
        AutopilotEvents.HILBERT_STARTED.invoker().onHilbertStarted(searchWidth, client.player.getChunkPos());
    }


    /**
     * Pauses the currently running Hilbert curve traversal and fires the {@link AutopilotEvents.EnabledEvent#onEnabled()} (int, ChunkPos, int)} event.
     */
    public static void stopHilbert() {
        runningHilbert = false;
        movingToTarget = false;
        AutopilotEvents.HILBERT_STOPPED.invoker().onHilbertStopped(hilbertCurve.regionWidth, hilbertCurve.startChunk, hilbertCurve.CurrentPoint);
        hilbertCurve = null;
    }

    /**
     * Updates the maximum travel speed of the autopilot.
     *
     * @param maxSpeed the new maximum speed in blocks per tick
     */
    public static void setMaxSpeed(double maxSpeed) {
        Autopilot.maxSpeed = maxSpeed;
        AutopilotEvents.MAX_SPEED_CHANGED.invoker().onMaxSpeedChanged(maxSpeed);
    }

    /**
     * Executes an autopilot update tick, progressing Hilbert traversal or movement towards the current target.
     *
     * @param client the Minecraft client instance
     */
    public static void autoPilotRun(MinecraftClient client) {
        if (client.player == null) {
            FargsClient.LOGGER.error("Player is null", new NullPointerException("Player is null"));
        }
        if (runningHilbert && !movingToTarget) {
            if (scanRunning) {
                return;
            }
            Waypoint waypoint = hilbertCurve.getNextWaypoint();
            if (waypoint != null) {
                targetLocation = waypoint.toPoint();
                movingToTarget = moveToPoint(client);
                return;
            }
            AutopilotEvents.HILBERT_FINISHED.invoker().onHilbertFinished();
            autoPilotMasterArm = false;
            runningHilbert = false;
            client.player.sendMessage(Text.literal("Scan job successfully finished"), false);
            return;
        }
        if (movingToTarget) {
            movingToTarget = moveToPoint(client);
        }
        autoPilotMasterArm = false;
        client.player.sendMessage(Text.literal("Target reached"), false);
    }

    /**
     * Sets the destination target to the specified point and begins navigating towards it.
     *
     * @param target the target point
     */
    public static void gotToTarget(Point target) {
        targetLocation = target;
        movingToTarget = true;
        AutopilotEvents.TARGET_CHANGED.invoker().onTargetChanged(target);
    }

    /**
     * Sets the destination target to the center of the specified chunk and begins navigating towards it.
     *
     * @param target the target chunk position
     */
    public static void gotToTarget(ChunkPos target) {
        targetLocation = new Point(target).mul(16).add(8);
        movingToTarget = true;
        AutopilotEvents.TARGET_CHANGED.invoker().onTargetChanged(targetLocation);
    }

    /**
     * Moves the player toward the target location according to {@link #maxSpeed}.
     *
     * @param client the Minecraft client instance
     * @return {@code true} if still in transit to the target, or {@code false} if the target was reached
     * @throws IllegalStateException    if the client player is null
     * @throws IllegalArgumentException if {@code maxSpeed} is not positive or {@code targetLocation} is not set
     */
    private static boolean moveToPoint(MinecraftClient client) throws IllegalStateException, IllegalArgumentException {
        if (client.player == null) {
            throw new IllegalStateException("Player is null");
        }
        if (maxSpeed <= 0) {
            throw new IllegalArgumentException("Max speed must be greater than 0");
        }
        if (targetLocation == null) {
            throw new IllegalArgumentException("Target location must be set");
        }
        double targetX = targetLocation.x + 0.5D;
        double targetZ = targetLocation.z + 0.5D;
        double deltaX = targetX - client.player.getX();
        double deltaZ = targetZ - client.player.getZ();
        double distance = Math.sqrt((deltaX * deltaX) + (deltaZ * deltaZ));
        if (distance <= maxSpeed) {
            client.player.setPosition(targetX, client.player.getY(), targetZ);
            AutopilotEvents.TARGET_REACHED.invoker().onTargetReached(targetLocation);
            return false;
        }
        double scale = maxSpeed / distance;
        client.player.setPosition(
                client.player.getX() + deltaX * scale,
                client.player.getY(),
                client.player.getZ() + deltaZ * scale
        );
        return true;
    }
}
