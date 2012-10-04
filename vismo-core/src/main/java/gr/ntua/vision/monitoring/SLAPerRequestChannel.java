package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.VismoAggregationResult;
import gr.ntua.vision.monitoring.sinks.EventSink;

import java.util.Map;


/**
 * 
 */
public class SLAPerRequestChannel implements EventSourceListener {
    /** this is used to get a hold of the whole dict, for serialization reasons. */
    private static final String DICT_KEY      = "!dict";
    /***/
    private static final String SPECIAL_FIELD = "transaction-duration";
    /***/
    private static final String topic         = "sla-per-request";
    /***/
    private final EventSink     sink;


    /**
     * Constructor.
     * 
     * @param sink
     */
    public SLAPerRequestChannel(final EventSink sink) {
        this.sink = sink;
    }


    /**
     * @see gr.ntua.vision.monitoring.EventSourceListener#receive(gr.ntua.vision.monitoring.events.Event)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void receive(final Event e) {
        if (!isCompleteObsEvent(e))
            return;

        @SuppressWarnings("rawtypes")
        final Map dict = (Map) e.get(DICT_KEY);

        dict.put("topic", topic);

        final VismoAggregationResult r = new VismoAggregationResult(dict);

        sink.send(r);
    }


    /**
     * Is this a complete object service event? Since we receive all events from object service, some of them are incomplete, in
     * the sense that contain parts of the request/response cycle.
     * 
     * @param e
     *            the event.
     * @return <code>true</code> iff the
     */
    private static boolean isCompleteObsEvent(final Event e) {
        return e.get(SPECIAL_FIELD) != null;
    }
}
