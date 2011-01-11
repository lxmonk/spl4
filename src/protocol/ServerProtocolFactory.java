package protocol;

/**
 * Protocol factory used by the reactor.
 * A new protocol instance is created for each connection.
 */
public interface ServerProtocolFactory {
    /**
     * Create a protocol instance for each connection.
     * @return a new protocol for each connection.
     */
    AsyncServerProtocol create();
}
