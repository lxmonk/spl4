package protocol.stomp;

import java.util.HashMap;

/**
 * Spl101 HW4 - Michael Elhadad Jan 2010
 * Connected Stomp Message:
 * Headers:
 * session
 */
public class ConnectedMsg extends Message {
    /**
     * Construct a connected message frame.
     * @param session  sessionId should be allocated by the server.
     */
    public ConnectedMsg( String session ) {
        super(Command.CONNECTED, new HashMap<String,String>(), null);
        headers().put("session", session);
    }
}
