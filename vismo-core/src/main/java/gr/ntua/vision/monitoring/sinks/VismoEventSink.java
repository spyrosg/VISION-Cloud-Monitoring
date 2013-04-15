package gr.ntua.vision.monitoring.sinks;

import gr.ntua.monitoring.sockets.Socket;
import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 * 
 */
public class VismoEventSink extends AbstractSink {
    /**
     * Constructor.
     * 
     * @param sock
     */
    public VismoEventSink(final Socket sock) {
        super(sock);
    }


    /**
     * @see gr.ntua.vision.monitoring.sinks.EventSink#send(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void send(final MonitoringEvent e) {
        final String s = serialize(e);

        send(s);
    }
}
