package tokenizer;

/**
 * Tokenizer factory used by reactor for each incoming connection.
 */
public interface TokenizerFactory {
    /**
     * @return  a new tokenizer instance for each connection.
     */
    StringMessageTokenizer create();
}
