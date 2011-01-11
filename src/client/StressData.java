package client;

/**
 * All the data shared by all agents of the stress client.
 */
public class StressData {
    public String host;
    public int port;
    boolean oneToOne;
    int messageSize;
    int delay;
    int nMessages;
    int nConnections;

    /**
     * Wrapper of all stress data in one container.
     * @param h hostname of server
     * @param p port of server
     * @param o connection mode - true = one to one, false = one to all.
     * @param mSize message size
     * @param d delay between each message sent by each connection
     * @param nm number of messages to send
     * @param nc number of connections to create
     */
    public StressData(String h, int p, boolean o, int mSize, int d, int nm, int nc) {
        this.host = h;
        this.port = p;
        this.oneToOne = o;
        this.messageSize = mSize;
        this.delay = d;
        this.nMessages = nm;
        this.nConnections = nc;
    }
}
