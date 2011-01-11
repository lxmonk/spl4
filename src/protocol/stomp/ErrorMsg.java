package protocol.stomp;
import java.util.HashMap;

/**
 * Spl101 HW4 - Michael Elhadad Jan 2010
 * Error Stomp Message:
 * Headers:
 * message
 */
public class ErrorMsg extends Message {
    /**
     * Construct an error message - called by server.
     * @param message is a short version
     * @param body gives details.
     */
    public ErrorMsg( String message, String body ) {
        super(Command.ERROR, new HashMap<String,String>(), body);
        headers().put("message", message);
    }
}
