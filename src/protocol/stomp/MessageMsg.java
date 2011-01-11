package protocol.stomp;

import java.util.HashMap;

/**
 * Spl101 HW4 - Michael Elhadad Jan 2010
 * Message Stomp Message:
 * Headers:
 * destination
 * message-id
 */
public class MessageMsg extends Message {
    /**
     * Construct a Message frame - called by server in reply to a SEND.
     * @param destination which received the message
     * @param messageId of the incoming message sent by another client
     * @param body of the incoming message sent by another client
     */
    public MessageMsg( String destination, String messageId, String body ) {
        super(Command.MESSAGE, new HashMap<String,String>(), body);
        headers().put("destination", destination);
        headers().put("message-id", messageId);
    }
}
