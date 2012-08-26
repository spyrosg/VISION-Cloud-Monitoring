package gr.ntua.vision.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ.Socket;


/**
 * The event distributor (TODO: better name) is used to pass events received in localhost to all <em>Vismo</em> consumers. Each
 * event is sent according to its accompanying topic. NOTE: the running thread does not block, which means that for now, if there
 * is no receiving end (no connected consumers) the event is dropped.
 */
public class EventDistributor implements EventListener {
    /** the log target. */
    private static final Logger log = LoggerFactory.getLogger(EventDistributor.class);
    /** the socket. */
    private final Socket        sock;


    /**
     * Constructor.
     * 
     * @param sock
     *            the socket to use.
     */
    EventDistributor(final Socket sock) {
        this.sock = sock;
    }


    /**
     * @see gr.ntua.vision.monitoring.EventListener#notify(java.lang.String)
     */
    @Override
    public void notify(final String message) {
        final boolean success = sock.send(message.getBytes(), 0);

        log.trace("sent: {}", success ? "ok" : "dropped");
    }
}
