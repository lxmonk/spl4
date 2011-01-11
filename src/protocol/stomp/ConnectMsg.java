package protocol.stomp;

import java.util.HashMap;

/**
 * Spl101 HW4 - Michael Elhadad Jan 2010
 * Connect Stomp Message:
 * Headers:
 * login
 * passcode
 */
public class ConnectMsg extends Message {
    /** 
     * Construct a Connect message request (used by client).
     * @param login of the incoming connection.
     * @param passcode of the incoming user.
     */
    public ConnectMsg( String login, String passcode ) {
        super(Command.CONNECT, new HashMap<String,String>(), null);
        headers().put("login", login);
        headers().put("passcode", passcode);
    }
}
