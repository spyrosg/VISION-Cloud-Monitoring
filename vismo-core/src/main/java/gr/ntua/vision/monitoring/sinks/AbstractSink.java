package gr.ntua.vision.monitoring.sinks;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.sockets.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is providing utility methods for transmitting an event through a socket.
 */
@SuppressWarnings("unused")
abstract class AbstractSink implements EventSink {
    /***/
    private static final Logger log    = LoggerFactory.getLogger(AbstractSink.class);
    /***/
    private volatile boolean    closed = false;
    /***/
    private final Socket        sock;


    /**
     * Constructor.
     * 
     * @param sock
     */
    public AbstractSink(final Socket sock) {
        this.sock = sock;
    }


    /**
     * @see gr.ntua.vision.monitoring.sinks.EventSink#close()
     */
    @Override
    public void close() {
        sock.close();
        closed = true;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<" + getClass().getSimpleName() + ": " + sock + ">";
    }


    /**
     * Transmit the string.
     * 
     * @param str
     *            a string.
     */
    protected void send(final String str) {
        if (closed)
            return;

        // log.trace("sending: {}", str);
        sock.send(str);
    }


    /**
     * Serialize the event.
     * 
     * @param e
     *            the event.
     * @return a string representation for the event.
     */
    protected String serialize(final MonitoringEvent e) {
        final String ser = e.serialize();

        if (sock.isZMQPUB()) {
            final String topic = e.topic();

            return (topic != null ? topic : "*") + " " + ser;
        }

        return ser;
    }
}
