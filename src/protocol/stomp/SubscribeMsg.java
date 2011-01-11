package protocol.stomp;
import java.util.HashMap;

/**
 * Spl101 HW4 - Michael Elhadad Jan 2010
 * Subscribe Stomp Message:
 * Headers:
 * destination
 * ack
 */
public class SubscribeMsg extends Message {
    /**
     * Construct a subscribe message - sent by client.
     * @param destination to which client subscribes.
     */
    public SubscribeMsg( String destination ) {
        super(Command.SUBSCRIBE, new HashMap<String,String>(), null);
        headers().put("destination", destination);
        // We only support auto ack-mode
        headers().put("ack", "auto");
    }
}
