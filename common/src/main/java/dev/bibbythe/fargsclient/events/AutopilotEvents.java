package dev.bibbythe.fargsclient.events;

import dev.bibbythe.fargsclient.types.Point;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.util.math.ChunkPos;

/**
 * Event callbacks for Autopilot actions and state changes.
 */
public interface AutopilotEvents {
    /**
     * Event instance for subscribing to and broadcasting Autopilot events.
     */
    Event<EnabledEvent> ENABLED = EventFactory.createLoop();
    Event<DisabledEvent> DISABLED = EventFactory.createLoop();
    Event<TargetChangedEvent> TARGET_CHANGED = EventFactory.createLoop();
    Event<TargetReachedEvent> TARGET_REACHED = EventFactory.createLoop();
    Event<HilbertStartedEvent> HILBERT_STARTED = EventFactory.createLoop();
    Event<HilbertPausedEvent> HILBERT_STOPPED = EventFactory.createLoop();
    Event<HilbertFinishedEvent> HILBERT_FINISHED = EventFactory.createLoop();
    Event<MaxSpeedChangedEvent> MAX_SPEED_CHANGED = EventFactory.createLoop();

    interface EnabledEvent {
        /**
         * Called when the autopilot system is enabled.
         */
        void onEnabled();
    }

    interface DisabledEvent {
        /**
         * Called when the autopilot system is disabled.
         */
        void onDisabled();
    }

    interface TargetChangedEvent {
        /**
         * Called when the navigation target changes.
         *
         * @param newTarget the new target coordinate
         */
        void onTargetChanged(Point newTarget);
    }

    interface TargetReachedEvent {
        /**
         * Called when the current target has been reached.
         *
         * @param reachedTarget the coordinate of the reached target
         */
        void onTargetReached(Point reachedTarget);
    }

    interface HilbertStartedEvent {
        /**
         * Called when a Hilbert curve traversal is started.
         *
         * @param searchWidth   the region size / search width for the traversal
         * @param startLocation the starting chunk coordinate
         */
        void onHilbertStarted(int searchWidth, ChunkPos startLocation);
    }

    interface HilbertPausedEvent {
        /**
         * Called when the Hilbert curve traversal is paused.
         *
         * @param searchWidth   the region size / search width for the traversal
         * @param startLocation the starting chunk coordinate
         * @param index         the index of the current point on the curve
         */
        void onHilbertStopped(int searchWidth, ChunkPos startLocation, int index);
    }

    interface HilbertFinishedEvent {
        /**
         * Called when the Hilbert curve traversal has finished visiting all waypoints.
         */
        void onHilbertFinished();
    }

    interface MaxSpeedChangedEvent {
        /**
         * Called when the autopilot max travel speed is updated.
         *
         * @param newMaxSpeed the new maximum speed in blocks per tick
         */
        void onMaxSpeedChanged(double newMaxSpeed);
    }

}
