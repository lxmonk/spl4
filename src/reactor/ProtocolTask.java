package reactor;

import java.nio.ByteBuffer;
import java.util.Vector;

import protocol.ServerProtocol;
import tokenizer.StringMessageTokenizer;

/**
 * This class supplies some data to the protocol, which then processes the data,
 * possibly returning a reply. This class is implemented as an executor task.
 * 
 */
public class ProtocolTask implements Runnable {
	private final ServerProtocol _protocol;
	private final StringMessageTokenizer _tokenizer;
	private final ConnectionHandler _handler;

	/**
	 * the fifo queue, which holds data coming from the socket. Access to the
	 * queue is serialized, to ensure correct processing order.
	 */
	private final Vector<ByteBuffer> _buffers = new Vector<ByteBuffer>();

	/**
	 * A protocol task remains alive as long as the connection is alive. It is
	 * reused each time new work is detected by the reactor and passed to the
	 * thread pool executor. The same task can be queued several times into the
	 * thread pool, in reaction to several events. Therefore, a task can be
	 * executed concurrently by several threads.
	 * 
	 * @param protocol
	 *            instance of the protocol of the specific connection.
	 * @param tokenizer
	 *            instance of the tokneizer of the specific connection - may
	 *            contain partially decoded bytes.
	 * @param h
	 *            connection handler that requires this task to be executed.
	 */
	public ProtocolTask(final ServerProtocol protocol,
			final StringMessageTokenizer tokenizer, final ConnectionHandler h) {
		this._protocol = protocol;
		this._tokenizer = tokenizer;
		this._handler = h;
	}

	/**
	 * Task: read as much as possible from incoming channel. then parse all
	 * complete messages and pass them to protocol instance. Synchronize on
	 * ourselves, in case we are executed by several threads from the thread
	 * pool.
	 */
	public synchronized void run() {
		// first, add all the bytes we have to the tokenizer
		synchronized (_buffers) {
			while (_buffers.size() > 0) {
				ByteBuffer buf = _buffers.remove(0);
				_tokenizer.addBytes(buf);
			}
		}
		// inc read couter
		reactor.Stats.readCounter.incrementAndGet();
		if (!_tokenizer.hasMessage()) {
			// each time a socket channel becomes readable, a protocol Task is
			// invoked but the tokenizer does not find a complete message, the
			// "Read Fragmentation Counter" is increased by one.
			reactor.Stats.readFragmentationCounter.incrementAndGet();
		}
		// now, go over all complete messages and process them.
		while (_tokenizer.hasMessage()) {
			String msg = _tokenizer.nextMessage();
			long start = System.currentTimeMillis();
			String response = _protocol.processMessage(msg, _handler);
			long end = System.currentTimeMillis();
			long delta = end - start;
			reactor.Stats.responseLatencyMs.addAndGet(delta);
			reactor.Stats.incomingMessages.incrementAndGet();
			if (response != null) {
				_handler.send(response);
			}
		}
	}

	/**
	 * Send bytes to the connected client.
	 * 
	 * @param b
	 *            buffers to be sent.
	 */
	public void addBytes(ByteBuffer b) {
		// we synchronize on _buffers and not on "this" because
		// run() is synchronized on "this", and it might take a long time
		// to run.
		synchronized (_buffers) {
			_buffers.add(b);
		}
	}
}
