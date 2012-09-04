package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.zmq.VismoSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The event distributor (TODO: better name) is used to pass events received in localhost to all <em>Vismo</em> consumers. Each
 * event is sent according to its accompanying topic. NOTE: the running thread does not block, which means that for now, if there
 * is no receiving end (no connected consumers) the event is dropped.
 */
public class EventDistributor {
	/** the log target. */
	private static final Logger log = LoggerFactory.getLogger(EventDistributor.class);
	/** the socket. */
	private final VismoSocket sock;

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

	/**
	 * @see gr.ntua.vision.monitoring.EventListener#notify(java.lang.String)
	 * @Override public void notify(final Event e) { // FIXME: wtf? !dict? really?
	 * @SuppressWarnings("rawtypes") final Map dict = (Map) e.get("!dict"); final boolean success =
	 *                               sock.send(JSONObject.toJSONString(dict));
	 * 
	 *                               log.trace("sent: {}", success ? "ok" : "dropped"); }
	 */
}
