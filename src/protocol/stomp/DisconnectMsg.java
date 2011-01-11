package protocol.stomp;

/**
 * Spl101 HW4 - Michael Elhadad Jan 2010.
 * Disconnect Stomp Message:
 */
public class DisconnectMsg extends Message {
    /**
     * Construct a disconnect message - called by client.
     */
    public DisconnectMsg( ) {
        super(Command.DISCONNECT, null, null);
    }
}
