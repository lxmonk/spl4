package protocol.stomp;

import reactor.Sender;
import protocol.AsyncServerProtocol;
import java.util.logging.Logger;

/**
 * An implementation of the STOMP protocol
 */
public class StompProtocol implements AsyncServerProtocol {
    private static final Logger logger = Logger.getLogger("edu.spl.reactor.stomp");
    private boolean _shouldClose = false;
    private boolean _connectionTerminated = false;
    private Stomp _state;

    /**
     * Each protocol instance refers to the same shared Server state.
     * @param state passed by the factory.
     * Each protocol instance may have its own connection-dependent state.
     * In a real Stomp server, it should at least be whether the connection is connected (logged in).
     */
    public StompProtocol(Stomp state) {
        _state = state;
    }

    /**
     * processes a message received as a frame encoded in UTF-16
     * Incoming messages: CONNECT, DISCONNECT, SUBSCRIBE, UNSUBSCRIBE, SEND
     *
     * @param msg the message to process (a frame without end delimiter)
     * @param sender who sent this message
     * @return always NULL except for the case of CONNECT (all messages are one-way)
     */
    public String processMessage(String msg, Sender sender) {
        if (this._connectionTerminated) {
            return null;
        }
        if (this.isEnd(msg)) {
            this._shouldClose = true;
            return null;
        }
        // Deserialize
        Message m = Message.fromString(msg);
        // logger.info("Deserialize: "+msg);
        // logger.info("Obtained: "+m.toString());
        if (m.command() == Command.CONNECT) {
            // @todo Allocate a session Id
            String sessionId = "sessionId";
            Message connected = new ConnectedMsg(sessionId);
            return connected.toString();
        } else if (m.command() == Command.SUBSCRIBE) {
            _state.subscribe(m.header("destination"), sender);
        } else if (m.command() == Command.UNSUBSCRIBE) {
            _state.unsubscribe(m.header("destination"), sender);
        } else if (m.command() == Command.SEND) {
            // @todo Allocate a message Id
            String messageId = "messageId";
            _state.send(m.header("destination"), messageId, m.body());
        }
        return null;
    }

    /**
     * determine whether the given message is the termination message
     *
     * @param msg the message to examine
     * @return false - this simple protocol doesn't allow termination...
     */
    public boolean isEnd(String msg) {
        return msg.startsWith("DISCONNECT");
    }

    /**
     * Is the protocol in a closing state?
     * @return true if the protocol is in closing state.
     */
    public boolean shouldClose() {
        return this._shouldClose;
    }

    /**
     * Indicate to the protocol that the client disconnected.
     */
    public void connectionTerminated() {
        this._connectionTerminated = true;
    }
}
