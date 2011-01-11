package reactor;

/**
 * Listener interface: abstracts between protocol and connectionHandler.
 */
public interface Sender {
    /**
     * Ask the sender object to receive the string s.
     * Invoked by protocol on subscribed connection handlers.
     * @param s message received by server and delegated to listener.
     */
    public void send(String s);
}
