package protocol.stomp;
import java.util.HashMap;

/**
 * Spl101 HW4 - Michael Elhadad Jan 2010
 * Unsubscribe Stomp Message:
 * Headers:
 * destination
 */
public class UnsubscribeMsg extends Message {
    /**
     * Construct an unsubscribe message - called by client.
     * @param destination from which client unsubscribes.
     */
    public UnsubscribeMsg( String destination ) {
        super(Command.UNSUBSCRIBE, new HashMap<String,String>(), null);
        headers().put("destination", destination);
    }
}
