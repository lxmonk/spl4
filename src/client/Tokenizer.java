package client;

import java.io.IOException;

/**
 * tokenizing an input stream into protocol specific messages.
 *
 */
public interface Tokenizer {
    /**
     * @return the next token, or null if no token is available. Pay attention
     *         that a null return value does not indicate the stream is closed,
     *         just that there is no message pending.
     * @throws IOException to indicate that the connection is closed.
     */
    String nextToken() throws IOException;

    /**
     * @return whether the input stream is still alive.
     */
    boolean isAlive();
}
