package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.zmq.VismoSocket;

import java.util.HashSet;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The event distributor (TODO: better name) is used to pass events received in localhost to all <em>Vismo</em> consumers. Each
 * event is sent according to its accompanying topic. NOTE: the running thread does not block, which means that for now, if there
 * is no receiving end (no connected consumers) the event is dropped.
 */
class EventDistributor {
	/** the log target. */
	private static final Logger log = LoggerFactory.getLogger(EventDistributor.class);
	/** the socket. */
	private final VismoSocket sock;
	/***/
	private final HashSet<String> eventIds = new HashSet<String>();

	/**
	 * Constructor.
	 * 
	 * @param sock
	 *            the socket to use.
	 */
	EventDistributor(final VismoSocket sock) {
		this.sock = sock;
		log.debug("using: {}", sock);
	}

	public void serialize(Event e) {
		@SuppressWarnings("rawtypes")
		final Map dict = (Map) e.get("!dict");

		if (!eventAlreadySent(dict)) {
			log.trace("sending event: {}", dict);
			sock.send(e.topic() + " " + JSONObject.toJSONString(dict));
		}
	}

	private boolean eventAlreadySent(Map map) {
		final String id = (String) map.get("id");

		if (eventIds.contains(id)) {
			log.error("dropping already sent event: {}", map);

			return true;
		} else {
			eventIds.add(id);

			return false;
		}
	}
}
