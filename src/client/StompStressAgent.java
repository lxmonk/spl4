package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import protocol.stomp.Message;
import protocol.stomp.ConnectMsg;
import protocol.stomp.SubscribeMsg;
import protocol.stomp.SendMsg;

public class StompStressAgent {
    private static final Logger _logger = Logger.getLogger("edu.spl.reactor.stomp.stress");
    private final int _id;
    private final Charset _charset;
    private StressData _data;
    private OutputStreamWriter _osw;
    private MessageTokenizer _tok;

    /**
     * Prepare an agent to create a single connection that produces stress on a Stomp server.
     * @param id unique identifier for this connection
     * @param d parameters of this stress agent
     */
    public StompStressAgent(int id, StressData d) {
        this._id = id;
        this._charset = Charset.forName("UTF-8");
        this._data = d;
        try {
            InetAddress address = InetAddress.getByName(this._data.host);
            Socket socket = new Socket(address, this._data.port);
            this._osw = new OutputStreamWriter(socket.getOutputStream(), this._charset);
            this._tok = new MessageTokenizer(new InputStreamReader(socket.getInputStream(), this._charset), '\0');
        } catch (UnknownHostException e ){
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Starts the stress agent.
     */
    public void init() {
        new Thread(new Sender()).start();
    }

    /**
     * Sends a serialized Stomp Message to the stream.
     * @param osw stream that handles charset encoding.
     * @param m Stomp message
     * @throws IOException when stream is closed.
     */
    private void send(OutputStreamWriter osw, Message m) throws IOException {
        this._logger.info("Stomp: " + m.toString());
        osw.write(m.toString() + '\0');
        osw.flush();
    }

    /** 
     * Generate a body of fixed size of the form "id id ...".
     * @param id of the agent 
     * @return string of fixed size 
     */
    private String generateBody(int id) {
	final int extra = 10;
        int size = this._data.messageSize;
        StringBuilder b = new StringBuilder(size+extra);
        while (b.length() < size) {
            b.append(id);
            b.append(' ');
        }
        return b.substring(0, size);
    }

    private class Sender implements Runnable {
        public void run() {
            try {
                // Stomp steps:
                // - Connect
                // - Subscribe
                // - Send x nMessages
                send(_osw, new ConnectMsg("login", "passcode"));
                String destination = "\\"+_id;
                if (_data.oneToOne) {
                    // Subscribe only to my queue
                    _logger.info("Subscribe " + _id + " to " + destination);
                    send(_osw, new SubscribeMsg(destination));
                } else {
                    // Subscribe to all queues 1...nConnections
                    for (int q = 1; q <= _data.nConnections; q++) {
                        _logger.info("Subscribe " + _id + " to \\" + q);
                        send(_osw, new SubscribeMsg("\\" + q));
                    }
                }
                // @todo: Wait for all agents to complete subscriptions
                try {
		    final int delayForSubscribe = 1000;
                    Thread.sleep(delayForSubscribe);
                } catch (InterruptedException e) {
                    return;
                }

                String body = generateBody(_id);
                for (int i = 0; i < _data.nMessages; i++) {
                    _logger.info("Sending to \\" + _id + ": " + body);
                    send(_osw, new SendMsg(destination, body));
                    try {
                        Thread.sleep(_data.delay);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private class Reader implements Runnable {
        public void run() {
            // @todo
        }
    }
}
