package dev.bibbythe.fargsclient.events;

import com.rabbitmq.client.Delivery;
import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.bibbythe.fargsclient.communication.types.datatypes.RegionRequestResponseData;

import java.util.ArrayList;
import java.util.Map;

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

    Event<blocklistUpdated> BLOCKLIST_UPDATED = EventFactory.createLoop();
    interface blocklistUpdated {
        void onUpdatedBlocklist(Map<String, ArrayList<String>> updatedBlocklist);
    }

    Event<regionRequestResponseReceived> REGION_REQUEST_RESPONSE_RECEIVED = EventFactory.createLoop();
    interface regionRequestResponseReceived {
        void onRegionRequestResponseReceived(RegionRequestResponseData message);
    }

    Event<updateAvailable> UPDATE_AVAILABLE = EventFactory.createLoop();
    interface updateAvailable {
        void onUpdateAvailable(String version);
    }

    Event<dimensionComplete> DIMENSION_COMPLETE = EventFactory.createLoop();
    interface dimensionComplete {
        void onDimensionComplete();
    }
}
