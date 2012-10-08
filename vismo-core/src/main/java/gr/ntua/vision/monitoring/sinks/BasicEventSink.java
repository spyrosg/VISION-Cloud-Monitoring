package gr.ntua.vision.monitoring.sinks;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.zmq.VismoSocket;

import java.util.HashSet;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class BasicEventSink implements EventSink {
	/***/
	private static final Logger log = LoggerFactory
			.getLogger(BasicEventSink.class);
	/***/
	protected final VismoSocket sock;
	// TODO: eventually remove eventIds or else vismo will blow with {@link
	// OutOfMemoryError}.
	/***/
	private final HashSet<String> eventIds = new HashSet<String>();

	/**
	 * Constructor.
	 * 
	 * @param sock
	 */
	public BasicEventSink(final VismoSocket sock) {
		this.sock = sock;
	}

	/**
	 * @see gr.ntua.vision.monitoring.sinks.EventSink#send(gr.ntua.vision.monitoring.events.Event)
	 */
	@Override
	public void send(final Event e) {
		@SuppressWarnings("rawtypes")
		final Map dict = (Map) e.get("!dict");
		final String ser = JSONObject.toJSONString(dict);

		if (!eventAlreadySent(dict)) {
			log.trace("sending event: {}", ser);
			send(e.topic(), ser);
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "#<BasicEventSink using " + sock + ">";
	}

	/**
	 * Check whether the event has already passed.
	 * 
	 * @param map
	 * @return <code>true</code> iff the event has already been seen, according
	 *         to its id, <code>false</code> otherwise.
	 */
	protected boolean eventAlreadySent(
			@SuppressWarnings("rawtypes") final Map map) {
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
	protected void send(final String topic, final String message) {
		sock.send(topic + " " + message);
	}
}
