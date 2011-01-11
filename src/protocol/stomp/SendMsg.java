package protocol.stomp;
import java.util.HashMap;

/**
 * Spl101 HW4 - Michael Elhadad Jan 2010
 * Send Stomp Message:
 * Headers:
 * destination
 */
public class SendMsg extends Message {
    /**
     * Construct a Send frame message - called by client.
     * @param destination to which the message is sent
     * @param body arbitrary string
     */
    public SendMsg( String destination, String body ) {
        super(Command.SEND, new HashMap<String,String>(), body);
        headers().put("destination", destination);
    }
}
