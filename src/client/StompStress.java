package client;

public class StompStress {
    /**
     * @param args Arguments:
     * <ol>
     * <li> Host where the Stomp Server is running
     * <li> Port on which the Stomp Server listens
     * <li> The subscription mode of the client: one-to-one or one-to-all
     * <li> Number of connections to open to the Stomp Server
     * <li> Size of the messages to send in bytes
     * <li> Delay between sending each message in milliseconds
     * <li> Number of messages to send
     * </ol>
     */
    public static void main(String[] args) {
	final int nParams = 7;
        if (args.length != nParams) {
            System.err.println("Usage: java StompStress host port [one-to-one|one-to-all] nConnections messageSize delay nMessages");
            return;
        }
        StressData d;
        try {
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            boolean oneToOne = (args[2].equals("one-to-one") ? true : false);
            int nConnections = Integer.parseInt(args[3]);
            int messageSize = Integer.parseInt(args[4]);
            int delay = Integer.parseInt(args[5]);
            int nMessages = Integer.parseInt(args[6]);
            // ignore checkstyle complaint about many params to ctor
            d = new StressData(host, port, oneToOne, messageSize, delay, nMessages, nConnections);
        } catch (Exception e) {
            System.err.println("Bad argument");
            System.err.println("Usage: java StompStress host port [one-to-one|one-to-all] nConnections messageSize delay nMessages");
            return;
        }
        StompStressAgent[] agents = new StompStressAgent[d.nConnections];
        for (int i = 0; i < d.nConnections; i++) {
            agents[i] = new StompStressAgent(i+1, d);
        }
        for (int i = 0; i < d.nConnections; i++) {
            agents[i].init();
        }
    }
}
