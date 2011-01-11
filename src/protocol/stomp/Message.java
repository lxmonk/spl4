package protocol.stomp;

import java.util.HashMap;
import java.util.Map;

/**
 * (c)2005 Sean Russell
 * Adapted to SPL101 HW4 - Michael Elhadad Jan 2010
 */
public class Message {
    private Command _command;
    private Map<String,String> _headers;
    private String _body;
    protected Message( Command c, Map<String,String> h, String b ) {
        _command = c;
        _headers = h;
        _body = b;
    }
    /**
     * Mutable accessor
     * @return headers of the Message.
     */
    public Map<String,String> headers() { return _headers; }
    /**
     * Immutable accessor 
     * @return body
     */
    public String body() { return _body; }
    /**
     * Immutable 
     * @return interned command of the message.
     */
    public Command command() { return _command; }
    /**
     * Extract specific header from message by name
     *
     * @param header name of header
     * @return value of header - null if not found.
     */
    public String header(String header) {
        return _headers.get(header);
    }

    /**
     * Format a message into a Stomp frame with no delimiter
     *
     * @return a valid Stomp frame ready to be framed by tokenizer getBytesForMessage()
     */
    public String toString() {
        StringBuffer message = new StringBuffer( _command.toString() );
        message.append( "\n" );
        if (_headers != null) {
            for ( String key : _headers.keySet() ) {
                String value = (String)_headers.get(key);
                message.append( key );
                message.append( ":" );
                message.append( value );
                message.append( "\n" );
            }
        }
        message.append( "\n" );
        if (_body != null) message.append( _body );
        return message.toString();
    }

    /**
     * @param m as returned from tokenizer contains no framing delimiter
     * General assumption that the frame is well formed.
     *
     * @return parsed message
     */
    static Message fromString(String m) {
        int ind1 = m.indexOf( '\n' );
        String vc = m.substring( 0, ind1 );
        Command c = Command.valueOf(vc); // Throws Error if unrecognized
        // Keep reading headers until empty line
        Map<String,String> headers = new HashMap<String,String>();
        int ind2 = m.indexOf( '\n', ++ind1 );
        while (ind2 > ind1) {
            String h = m.substring( ind1, ind2 );
            int p = h.indexOf( ':' );
            String k = h.substring( 0, p );
            String v = h.substring( p+1 );
            headers.put(k.trim(),v.trim());
            ind1 = ind2+1;
            ind2 = m.indexOf('\n', ind1);
        }
        String body = m.substring( ind1+1 );

        return new Message(c, headers, body);
    }
}

