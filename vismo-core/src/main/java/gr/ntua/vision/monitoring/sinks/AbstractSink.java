package gr.ntua.vision.monitoring.sinks;

import gr.ntua.vision.monitoring.zmq.VismoSocket;

import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractSink implements EventSink {
	/***/
	private static final Logger log = LoggerFactory.getLogger(AbstractSink.class);
	/***/
	protected final VismoSocket sock;
	// TODO: eventually remove eventIds or else vismo will blow with {@link
	// OutOfMemoryError}.
	/***/
	private final HashSet<String> eventIds = new HashSet<String>();

	/**
	 * Constructor.
	 */
	public AbstractSink(VismoSocket sock) {
		this.sock = sock;
	}

	/**
	 * Check whether the event has already passed.
	 * 
	 * @param map
	 * @return <code>true</code> iff the event has already been seen, according
	 *         to its id, <code>false</code> otherwise.
	 */
	protected boolean eventAlreadySent(@SuppressWarnings("rawtypes") final Map map) {
		final String id = (String) map.get("id");

		if (eventIds.contains(id)) {
			log.error("dropping already sent event: {}", map);
			return true;
		}

		eventIds.add(id);

		return false;
	}

	/**
	 * @param message
	 */
	protected void send(final String message) {
		sock.send(message);
	}
}
