package client;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Tokenizer on char delimiter framing.
 * Uses blocking IO
 */
public class MessageTokenizer implements Tokenizer {
    public final char _delimiter;
    private final InputStreamReader _isr;
    private boolean _closed;

    /**
     * Will tokenize from bytes into strings up to delimiter
     *
     * @param isr input stream that already handles charset decoding
     * @param delimiter will not be included in tokens
     */
    public MessageTokenizer (InputStreamReader isr, char delimiter) {
        this._delimiter = delimiter;
        this._isr = isr;
        this._closed = false;
    }

    /**
     * Return string containing one frame of the protocol without delimiter.
     * @throws IOException when stream is closed by other side
     * @return a string without delimiter containing a full protocol frame
     */
    public String nextToken() throws IOException {
        if (!this.isAlive()) {
            throw new IOException("tokenizer is closed");
	}
        String ans = null;
        try {
            // we are using a blocking stream, so we should always end up
            // with a message, or with an exception indicating an error in
            // the connection.
            int c;
            StringBuilder sb = new StringBuilder();
            // read char by char, until encountering the framing character, or
            // the connection is closed.
            while ((c = this._isr.read()) != -1) {
                if (c == this._delimiter) {
                    break;
		} else {
                    sb.append((char) c);
		}
            }
            ans = sb.toString();
        } catch (IOException e) {
            this._closed = true;
            throw new IOException("Connection is dead");
        }
        return ans;
    }

    /**
     * Remains true as long as underlying input stream is not closed.
     * @return true when stream is not closed.
     */
    public final boolean isAlive() {
        return !this._closed;
    }

}
