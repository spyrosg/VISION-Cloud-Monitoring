package gr.ntua.vision.monitoring.notify;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import org.zeromq.ZContext;


/**
 *
 */
public class VismoEventRegistry extends EventRegistry {
    /** FIXME */
    private final static VismoConfiguration conf = null;
    /***/
    private static final ZMQSockets         zmq  = new ZMQSockets(new ZContext());


    /**
     * Constructor.
     */
    public VismoEventRegistry() {
        super(zmq, conf.getConsumersPoint());
    }


    /**
     * Constructor.
     * 
     * @param debug
     *            when <code>true</code>, it activates the console logger for this package.
     */
    public VismoEventRegistry(final boolean debug) {
        super(zmq, conf.getConsumersPoint(), debug);
    }
}
