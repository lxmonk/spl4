package reactor;

import java.util.logging.Logger;
import java.nio.charset.Charset;
import protocol.*;
import protocol.stomp.*;
import tokenizer.*;

/**
 * An implementation of the Reactor pattern.
 */
public class StompReactor {
    private static final Logger logger = Logger.getLogger("edu.spl.reactor.stomp");
    private final Reactor _reactor;

    /**
     * Creates a new StompReactor
     *
     * @param poolSize
     *            the number of WorkerThreads to include in the ThreadPool
     * @param port
     *            the port to bind the Reactor to
     */
    public StompReactor(int port, int poolSize) {
        final StompProtocolFactory protocolMaker = new StompProtocolFactory();
        final Charset charset = Charset.forName("UTF-8");
        final TokenizerFactory tokenizerMaker = new TokenizerFactory() {
                public StringMessageTokenizer create() {
                    return new FixedSeparatorMessageTokenizer("\000", charset);
                }
            };
        _reactor = new Reactor(port, poolSize, protocolMaker, tokenizerMaker);
    }

    /**
     * @return active reactor
     */
    public Reactor reactor() {
        return _reactor;
    }

    /**
     * Main program: StompReactor
     * Reactor-based server for the Stomp protocol. Listening port number and
     * number of threads in the thread pool are read from the commandline.
     *
     * @param args command line arguments:
     *   port poolSize
     */
    public static void main(String args[]) {
        if (args.length != 2) {
            System.err.println("Usage: java StompReactor <port> <pool_size>");
            System.exit(1);
        }
        try {
            int port = Integer.parseInt(args[0]);
            int poolSize = Integer.parseInt(args[1]);
            StompReactor sr = new StompReactor(port, poolSize);
            Thread thread = new Thread(sr.reactor());
            thread.start();
            logger.info("StompReactor is ready on port " + sr.reactor().getPort());
            // @todo: get report command from System.in and process report
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
