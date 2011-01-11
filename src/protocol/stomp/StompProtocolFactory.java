package protocol.stomp;

import protocol.ServerProtocolFactory;
import protocol.AsyncServerProtocol;

public class StompProtocolFactory implements ServerProtocolFactory {
    private Stomp state;
    /**
     * Prepare a shared Stomp state to be shared by all protocol instances.
     */
    public StompProtocolFactory() {
        state = new Stomp();
    }
    /**
     * Create a new protocol instance for each new connection and link it
     * to the shared server state.
     * @return new protocol instance for each connection.
     */
    public AsyncServerProtocol create() {
        return new StompProtocol(state);
    }
}
