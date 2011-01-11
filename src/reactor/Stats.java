/**
 * 
 */
package reactor;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Tom
 * @author Aviad
 * 
 *         This class collects the statistics for the run of the server and will
 *         print them on demand.
 * 
 */
public class Stats {

	static AtomicLong incomingBytes = new AtomicLong();
	static AtomicLong outgoingBytes = new AtomicLong();
	static AtomicLong connectionsNumber = new AtomicLong();
	static AtomicLong incomingMessages = new AtomicLong();
	static AtomicLong readCounter = new AtomicLong(1);
	static AtomicLong readFragmentationCounter = new AtomicLong();
	static AtomicLong writeCounter = new AtomicLong(1);
	static AtomicLong writeFragmentationCounter = new AtomicLong();
	static AtomicLong connectionLatencyMs = new AtomicLong();
	static AtomicLong responseLatencyMs = new AtomicLong();

	/**
	 * print
	 */
	static void print() {
		System.out.println("Incoming bytes: " + incomingBytes.toString());
		System.out.println("Outgoing bytes: " + outgoingBytes.toString());
		System.out.println("Connections number: "
				+ connectionsNumber.toString());
		System.out.println("Incoming messages: " + incomingMessages.toString());
		System.out.println("Connection latency: "
				+ connectionLatencyMs.toString() + " msec -- "
				+ connectionLatencyMs.get() / (double) connectionsNumber.get()
				+ " msec per connection average");
		System.out.println("Response latency: " + responseLatencyMs.toString()
				+ " msec -- " + responseLatencyMs.get()
				/ (double) incomingMessages.get()
				+ " msec per response average");
		System.out.println("Read Fragmentation Level: "
				+ readFragmentationCounter.get() / (float) readCounter.get());
		System.out.println("Write Fragmentation Level: "
				+ writeFragmentationCounter.get() / (float) writeCounter.get());

	}
}
