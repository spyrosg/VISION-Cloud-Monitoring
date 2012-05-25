package gr.ntua.vision.monitoring.events;

import org.zeromq.ZContext;


/**
 *
 */
public class VismoEventRegistry extends EventRegistry {
    /***/
    private static final String DISTRIBUTION_POINT = "tcp://127.0.0.1:27890";


    /**
     * Constructor.
     */
    public VismoEventRegistry() {
        super(new ZContext(), DISTRIBUTION_POINT);
    }
}
