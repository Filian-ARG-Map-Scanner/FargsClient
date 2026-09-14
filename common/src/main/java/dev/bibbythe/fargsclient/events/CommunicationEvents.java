package dev.bibbythe.fargsclient.events;

import com.rabbitmq.client.Delivery;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;

public interface CommunicationEvents {
    Event<RecoverySuccess> RECOVERY_SUCCESS = EventFactory.createLoop();
    interface RecoverySuccess {
        void onRecoverySuccess();
    }

    Event<RecoveryStarted> RECOVERY_STARTED = EventFactory.createLoop();
    interface RecoveryStarted {
        void onRecoveryStarted();
    }
    Event<ReceivedDirectMessage> RECEIVED_DIRECT_MESSAGE = EventFactory.createLoop();
    interface ReceivedDirectMessage {
        void onReceivedDirectMessage(String consumerTag, Delivery delivery);
    }
    Event<ReceivedFanoutMessage> RECEIVED_FANOUT_MESSAGE = EventFactory.createLoop();
    interface ReceivedFanoutMessage {
        void onReceivedFanoutMessage(String consumerTag, Delivery delivery);
    }
}
