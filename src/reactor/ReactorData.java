package reactor;

import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;

import protocol.ServerProtocolFactory;
import tokenizer.TokenizerFactory;

/**
 * a simple data stucture that hold information about the reactor, including getter methods
 */
public class ReactorData {

    private final ExecutorService _executor;
    private final Selector _selector;
    private final ServerProtocolFactory _protocolMaker;
    private final TokenizerFactory _tokenizerMaker;
    private final Stats _stats;

    /**
     * Accessor.
     * @return thread pool executor
     */
    public ExecutorService getExecutor() {
        return _executor;
    }

    /**
     * Accessor.
     * @return selector
     */
    public Selector getSelector() {
        return _selector;
    }

    /**
     * Bundle all reactor state in one object.
     * @param executor thread pool
     * @param selector to multiplex events from all incoming channels
     * @param protocol factory to create specific protocol handlers for each connection
     * @param tokenizer factory to create specific tokenizers for each connection
     * @param stats of this server
     */
	public ReactorData(ExecutorService executor, Selector selector, 
			ServerProtocolFactory protocol, TokenizerFactory tokenizer, Stats stats) {
        this._executor = executor;
        this._selector = selector;
        this._protocolMaker = protocol;
        this._tokenizerMaker = tokenizer;
        this._stats = stats;
    }

    /**
     * Accessor.
     * @return protocol factory to create a new protocol instance for each connection.
     */
    public ServerProtocolFactory getProtocolMaker() {
        return _protocolMaker;
    }

    /**
     * Accessor.
     * @return tokenizer factory to create a new tokenizer instance for each connection.
     */
    public TokenizerFactory getTokenizerMaker() {
        return _tokenizerMaker;
    }
    
    /**
     * return the {@link Stats} object of this server
     * @return the {@link Stats} object of this server
     */
    public Stats getStats() {
    	return _stats;
    }
}
