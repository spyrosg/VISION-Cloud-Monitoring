package gr.ntua.vision.monitoring.sinks;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.zmq.VismoSocket;

import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class PubSubEventSink extends AbstractSink {
    /***/
    private static final Logger log = LoggerFactory.getLogger(PubSubEventSink.class);


    /**
     * Constructor.
     * 
     * @param sock
     */
    public PubSubEventSink(final VismoSocket sock) {
        super(sock);
    }


    /**
     * @see gr.ntua.vision.monitoring.sinks.EventSink#send(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void send(final Event e) {
        @SuppressWarnings("rawtypes")
        final Map dict = (Map) e.get("!dict");
        final String ser = JSONObject.toJSONString(dict);

        if (!eventAlreadySent((String) e.get("id"))) {
            log.trace("sending event: {}", ser);
            send(e.topic() + " " + ser);
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<PubSubEventSink using " + sock + ">";
    }
}
