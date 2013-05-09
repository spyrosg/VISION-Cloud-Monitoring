package gr.ntua.vision.monitoring.sinks;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.sockets.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to serialize and send out events "<code>as is</code>".
 */
public class PlainEventSink extends AbstractSink {
    /***/
    private static final Logger log = LoggerFactory.getLogger(PlainEventSink.class);


    /**
     * Constructor.
     * 
     * @param sock
     */
    public PlainEventSink(final Socket sock) {
        super(sock);
    }


    /**
     * @see gr.ntua.vision.monitoring.sinks.EventSink#send(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void send(final MonitoringEvent e) {
        final String s = e.serialize();

        log.trace("serializing: {}", s);
        send(s);
    }
}
