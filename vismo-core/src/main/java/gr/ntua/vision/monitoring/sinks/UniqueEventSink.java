package gr.ntua.vision.monitoring.sinks;

import gr.ntua.monitoring.sockets.Socket;
import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to transmit events to a socket, if the event has not been encountered before.
 */
public class UniqueEventSink extends AbstractSink {
    /** the log target. */
    private static final Logger   log      = LoggerFactory.getLogger(UniqueEventSink.class);
    // TODO: eventually remove eventIds or else vismo will blow with {@link OutOfMemoryError}.
    /***/
    private final HashSet<String> eventIds = new HashSet<String>();


    /**
     * Constructor.
     * 
     * @param sock
     */
    public UniqueEventSink(final Socket sock) {
        super(sock);
    }


    /**
     * @see gr.ntua.vision.monitoring.sinks.EventSink#send(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void send(final MonitoringEvent e) {
        sendOrDrop(e);
    }


    /**
     * Check that we have not seen an event with the same id.
     * 
     * @param id
     *            the event id.
     * @return <code>true</code> iff we have not seen the id.
     */
    private boolean haveSeenEvent(final String id) {
        return eventIds.contains(id);
    }


    /**
     * Serialize and transmit the event.
     * 
     * @param e
     *            the event to send.
     */
    private void sendOrDrop(final MonitoringEvent e) {
        final String id = (String) e.get("id");

        if (haveSeenEvent(id)) {
            log.error("dropping already sent event with id: {}", id);
            return;
        }

        eventIds.add(id);
        final String s = serialize(e);

        log.trace("sending event: {}", s);
        send(s);
    }
}
