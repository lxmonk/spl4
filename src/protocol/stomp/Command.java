package protocol.stomp;

/**
 * (c)2005 Sean Russell Adapted to SPL101 HW4 - Michael Elhadad Jan 2010
 */
public class Command {
	public final static String ENCODING = "UTF-8";
	private String _command;

	private Command(String msg) {
		_command = msg;
	}

	public static Command SEND = new Command("SEND"), SUBSCRIBE = new Command(
			"SUBSCRIBE"), UNSUBSCRIBE = new Command("UNSUBSCRIBE"),
			DISCONNECT = new Command("DISCONNECT"), CONNECT = new Command(
					"CONNECT");

	public static Command MESSAGE = new Command("MESSAGE"),
			CONNECTED = new Command("CONNECTED"), ERROR = new Command("ERROR");

	/**
	 * Map a string to a static unique command (this operation is called
	 * "interning").
	 * 
	 * @param inStr
	 *            string to be parsed.
	 * @return an interned Command object.
	 */
	public static Command valueOf(String inStr) {
		String v = inStr.trim();
		if (v.equals("SEND"))
			return SEND;
		else if (v.equals("SUBSCRIBE"))
			return SUBSCRIBE;
		else if (v.equals("UNSUBSCRIBE"))
			return UNSUBSCRIBE;
		else if (v.equals("CONNECT"))
			return CONNECT;
		else if (v.equals("MESSAGE"))
			return MESSAGE;
		else if (v.equals("CONNECTED"))
			return CONNECTED;
		else if (v.equals("DISCONNECT"))
			return DISCONNECT;
		else if (v.equals("ERROR"))
			return ERROR;
		throw new Error("Unrecognised command " + v);
	}

	/**
	 * @return Name of the command
	 */
	public String toString() {
		return _command;
	}
}
