package gr.ntua.vision.monitoring.sinks;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.sockets.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is a specialization of {@link AbstractSink}, that knows how to handle events with topic.
 */
public class TopicedEventSink extends AbstractSink {
    /***/
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TopicedEventSink.class);


    /**
     * Constructor.
     * 
     * @param sock
     */
    public TopicedEventSink(final Socket sock) {
        super(requirePubSocket(sock));
    }


    /**
     * @see gr.ntua.vision.monitoring.sinks.EventSink#send(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void send(final MonitoringEvent e) {
        final String s = e.topic() + " " + e.serialize();

        send(s);
    }


    /**
     * @param sock
     * @return the provided socket.
     */
    private static Socket requirePubSocket(final Socket sock) {
        if (!sock.isZMQPUB())
            throw new Error("socket should be a pub socket");

        return sock;
    }
}
