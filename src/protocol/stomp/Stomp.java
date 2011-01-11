package protocol.stomp;

import java.util.*;
import reactor.Sender;
import java.util.logging.Logger;

/**
 * Stomp server state
 * Maintain global state of the Stomp server: set of queues with their subscriptions
 */
public class Stomp {
    private static final Logger logger = Logger.getLogger("edu.spl.protocol.stomp");
    /**
     * A multimap of destination => Sender pairs.
     */
    private Map<String, List<Sender>> _listeners = new HashMap<String, List<Sender>>();

    /**
     * Construct state of the Stomp server: No specific dependencies.
     */
    public Stomp() {}

    /**
     * Subscribe to a channel.
     *
     * @param name The name of the channel to which the listener subscribes
     * @param sender A listener to receive messages sent to the channel
     */
    public void subscribe( String name,  Sender sender ) {
        logger.info("Subscribe "+name+" "+sender);
        synchronized (_listeners) {
            if (_listeners != null) {
                List<Sender> list = _listeners.get( name );
                if (list == null) {
                    list = new ArrayList<Sender>();
                    _listeners.put( name, list );
                }
                if (!list.contains( sender )) list.add( sender );
            }
        }
    }

    /**
     * Unsubscribe a single listener from a channel.
     *
     * @param name The name of the channel to unsubscribe from.
     * @param l The listener to unsubscribe
     */
    public void unsubscribe( String name, Sender l ) {
        logger.info("Unsubscribe "+name+" "+l);
        synchronized (_listeners) {
            List<Sender> list = _listeners.get( name );
            if (list != null) {
                list.remove( l );
                if (list.size() == 0) {
                    _listeners.remove( name );
                }
            }
        }
    }

    /**
     * Send a message to a destination
     *
     * @param name of the destination queue
     * @param messageId is allocated by the client
     * @param body sent by the client
     */
    public void send( String name, String messageId, String body) {
        logger.info("Send "+name+": "+body);
        List<Sender> senders = null;
        // Snapshot
        synchronized (_listeners) {
            senders = new ArrayList<Sender>(_listeners.get( name ));
        }
        if (senders != null) {
            Message m = new MessageMsg(name, messageId, body);
            String ms = m.toString();
            for (Sender s : senders) {
                s.send(ms);
            }
        }
    }
}
