package gr.ntua.vision.monitoring.perf;

import gr.ntua.vision.monitoring.dispatch.EventDispatcher;


/**
 * 
 */
public class ConstantSizeEventService implements EventService {
    /***/
    public static final int       JSON_DIFF = 207;
    /***/
    private final EventDispatcher dispatcher;


    /**
     * Constructor.
     * 
     * @param dispatcher
     */
    public ConstantSizeEventService(final EventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }


    /**
     * @see gr.ntua.vision.monitoring.perf.EventService#send(int, long)
     */
    @Override
    public void send(final int noEvents, final long size) {
        final String dummyValue = getStringOf(size - JSON_DIFF);

        for (int i = 0; i < noEvents; ++i)
            dispatcher.newEvent().field("dummy", dummyValue).send();
    }


    /**
     * @see gr.ntua.vision.monitoring.perf.EventService#send(java.lang.String, int, long)
     */
    @Override
    public void send(final String topic, final int noEvents, final long size) {
        final String dummyValue = getStringOf(size - JSON_DIFF);

        for (int i = 0; i < noEvents; ++i)
            dispatcher.newEvent().field("topic", topic).field("dummy", dummyValue).send();
    }


    /**
     * @param size
     * @return a string of size <code>size</code> in bytes.
     */
    private static String getStringOf(final long size) {
        final StringBuilder buf = new StringBuilder();

        for (int i = 0; i < size; ++i)
            buf.append('a');

        return buf.toString();
    }
}
