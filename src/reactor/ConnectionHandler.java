package reactor;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.util.Vector;
import java.util.logging.Logger;

import protocol.AsyncServerProtocol;
import tokenizer.StringMessageTokenizer;

/**
 * Handles messages from clients
 */
public class ConnectionHandler implements Sender {
    private static final int BUFFER_SIZE = 1024;
    protected final SocketChannel _sChannel;
    protected final ReactorData _data;
    protected final AsyncServerProtocol _protocol;
    protected final StringMessageTokenizer _tokenizer;
    protected Vector<ByteBuffer> _outData = new Vector<ByteBuffer>();
    protected final SelectionKey _skey;
    private static final Logger logger = Logger.getLogger("edu.spl.reactor");
    private ProtocolTask _task = null;

    /**
     * Creates a new ConnectionHandler object
     *
     * @param sChannel
     *            the SocketChannel of the client
     * @param data
     *            a reference to a ReactorData object
     */
    private ConnectionHandler(SocketChannel sChannel, ReactorData data, SelectionKey key) {
        _sChannel = sChannel;
        _data = data;
        _protocol = _data.getProtocolMaker().create();
        _tokenizer = _data.getTokenizerMaker().create();
        _skey = key;
    }

    // make sure 'this' does not escape b4 the object is fully constructed!
    private void initialize() {
        _skey.attach(this);
        _task = new ProtocolTask(_protocol, _tokenizer, this);
    }

    /**
     * Static factory method (ctor is private).
     * This way we make sure reference to this does not escape through the selector before the
     * connectionHandler is fully constructed.
     * @param sChannel non-blocking socket channel just accepted from server socket.
     * @param data all Reactor data needed by handler.
     * @param key selector key to which sChannel is connected.
     * @return fully constructed connectionHandler.
     */
    public static ConnectionHandler create(SocketChannel sChannel, ReactorData data, SelectionKey key) {
        ConnectionHandler h = new ConnectionHandler(sChannel, data, key);
        h.initialize();
        return h;
    }

    /**
     * Send bytes to the connected client.
     * Selector will remain in Write mode until all data is sent.
     * 
     * @param buf bytes to be sent.
     */
    public synchronized void addOutData(ByteBuffer buf) {
        _outData.add(buf);
        switchToReadWriteMode();
    }

    private void closeConnection() {
        // remove from the selector.
        _skey.cancel();
        try {
            _sChannel.close();
        } catch (IOException ignored) {
            ignored = null;
        }
    }

    /**
     * Reads incoming data from the client:
     * <UL>
     * <LI>Reads some bytes from the SocketChannel
     * <LI>create a protocolTask, to process this data, possibly generating an
     * answer
     * <LI>Inserts the Task to the ThreadPool
     * </UL>
     *
     */
    public void read() {
        // do not read if protocol has terminated. only write of pending data is
        // allowed
        if (_protocol.shouldClose()) {
            return;
        }

        SocketAddress address = _sChannel.socket().getRemoteSocketAddress();
        logger.info("Reading from " + address);

        ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
        int numBytesRead = 0;
        try {
            numBytesRead = _sChannel.read(buf);
        } catch (IOException e) {
            numBytesRead = -1;
        }
        // is the channel closed?
        if (numBytesRead == -1) {
            // No more bytes can be read from the channel
            logger.info("client on " + address + " has disconnected");
            closeConnection();
            // tell the protocol that the connection terminated.
            _protocol.connectionTerminated();
            return;
        }

        //add the buffer to the protocol task
        buf.flip();
        _task.addBytes(buf);
        // add the protocol task to the reactor
        _data.getExecutor().execute(_task);
    }

    /**
     * attempts to send data to the client<BR>
     * if all the data has been succesfully sent, the ConnectionHandler will
     * automatically switch to read only mode, otherwise it'll stay in its
     * current mode (which is read / write).
     */
    public synchronized void write() {
        if (_outData.size() == 0) {
            // if nothing left in the output string, go back to read mode
            switchToReadOnlyMode();
            return;
        }
        // if there is something to send
        ByteBuffer buf = _outData.remove(0);
        if (buf.remaining() != 0) {
            try {
                _sChannel.write(buf);
            } catch (IOException e) {
                // If client closed connection without reading
                closeConnection();
                SocketAddress address = _sChannel.socket().getRemoteSocketAddress();
                logger.info("client disconnected on " + address);
            }
            // check if the buffer contains more data
            if (buf.remaining() != 0) {
                _outData.add(0, buf);
            }
        }
        // check if the protocol indicated close.
        if (_protocol.shouldClose()) {
            switchToWriteOnlyMode();
            if (buf.remaining() == 0) {
                closeConnection();
                SocketAddress address = _sChannel.socket().getRemoteSocketAddress();
                logger.info("disconnecting client on " + address);
            }
        }
    }

    /**
     * switches the handler to read / write TODO Auto-generated catch blockmode
     */
    public void switchToReadWriteMode() {
        try {
            _skey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            _data.getSelector().wakeup();
        } catch (CancelledKeyException e) {
            logger.info("Client disconnected");
        }
    }

    /**
     * switches the handler to read only mode
     */
    public void switchToReadOnlyMode() {
        try {
	    _skey.interestOps(SelectionKey.OP_READ);
	    _data.getSelector().wakeup();
        } catch (CancelledKeyException e) {
            logger.info("Client disconnected");
        }
    }

    /**
     * switches the handler to write only mode
     */
    public void switchToWriteOnlyMode() {
        try {
	    _skey.interestOps(SelectionKey.OP_WRITE);
	    _data.getSelector().wakeup();
        } catch (CancelledKeyException e) {
            logger.info("Client disconnected");
        }
    }

    /**
     * Frame and send a string to client
     * Invoked by protocol when it needs to send to various clients.
     *
     * @param s string to be framed (not encoded, no delimiter)
     */
    public void send(String s) {
        try {
            ByteBuffer bytes = _tokenizer.getBytesForMessage(s);
            addOutData(bytes);
        } catch (CharacterCodingException e) { e.printStackTrace(); }
    }
}
