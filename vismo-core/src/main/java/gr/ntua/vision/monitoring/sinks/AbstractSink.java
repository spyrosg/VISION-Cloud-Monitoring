package gr.ntua.vision.monitoring.sinks;

import gr.ntua.vision.monitoring.zmq.VismoSocket;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
abstract class AbstractSink implements EventSink {
    /***/
    private static final Logger   log      = LoggerFactory.getLogger(AbstractSink.class);
    /***/
    protected final VismoSocket   sock;
    // TODO: eventually remove eventIds or else vismo will blow with {@link OutOfMemoryError}.
    /***/
    private final HashSet<String> eventIds = new HashSet<String>();


    /**
     * Constructor.
     * 
     * @param sock
     */
    public AbstractSink(final VismoSocket sock) {
        this.sock = sock;
    }


    /**
     * Check whether the event has already passed.
     * 
     * @param id
     * @return <code>true</code> iff the event has already been seen, according to its id, <code>false</code> otherwise.
     */
    protected boolean eventAlreadySent(final String id) {
        if (eventIds.contains(id)) {
            log.error("dropping already sent event with id: {}", id);
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
