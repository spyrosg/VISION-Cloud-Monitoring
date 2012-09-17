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
    private static final Logger   log      = LoggerFactory.getLogger(EventDistributor.class);
    // TODO: eventually remove eventIds or else vismo will blow with {@link OutOfMemoryError}.
    /***/
    private final HashSet<String> eventIds = new HashSet<String>();
    /** the socket. */
    private final VismoSocket     sock;


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
     * @param e
     */
    public void serialize(final Event e) {
        @SuppressWarnings("rawtypes")
        final Map dict = (Map) e.get("!dict");
        final String ser = JSONObject.toJSONString(dict);

        if (!eventAlreadySent(dict)) {
            log.trace("sending event: {}", ser);
            sock.send(e.topic() + " " + ser);
        }
    }


    /**
     * Check whether the event has already passed.
     * 
     * @param map
     * @return <code>true</code> iff the event has already been seen, according to its id, <code>false</code> otherwise.
     */
    private boolean eventAlreadySent(@SuppressWarnings("rawtypes") final Map map) {
        final String id = (String) map.get("id");

        if (eventIds.contains(id)) {
            log.error("dropping already sent event: {}", map);
            return true;
        }

        eventIds.add(id);

        return false;
    }
}
