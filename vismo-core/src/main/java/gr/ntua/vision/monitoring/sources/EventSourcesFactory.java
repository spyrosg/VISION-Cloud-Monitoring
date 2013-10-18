package gr.ntua.vision.monitoring.sources;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import org.zeromq.ZContext;


/**
 * Configure and provide the system with the appropriate event sources.
 */
public class EventSourcesFactory {
    /** the configuration object. */
    private final VismoConfiguration  conf;
    /***/
    private final EventSourceListener listener;
    /***/
    private final ZMQFactory          socketFactory;


    /**
     * Constructor.
     * 
     * @param conf
     *            the configuration object.
     * @param listener
     */
    public EventSourcesFactory(final VismoConfiguration conf, final EventSourceListener listener) {
        this(conf, new ZMQFactory(new ZContext()), listener);
    }


    /**
     * Constructor.
     * 
     * @param conf
     *            the configuration object.
     * @param socketFactory
     * @param listener
     */
    public EventSourcesFactory(final VismoConfiguration conf, final ZMQFactory socketFactory, final EventSourceListener listener) {
        this.conf = conf;
        this.socketFactory = socketFactory;
        this.listener = listener;
    }


    /**
     * Setup the sources group for cloud head.
     * 
     * @return an {@link EventSources} object.
     */
    public EventSources buildForCloudHead() {
        return new EventSources().append(localSource()).append(sourceforAddress(withPort(conf.getCloudHeadPort())));
    }


    /**
     * Setup the sources group for cluster head.
     * 
     * @return an {@link EventSources} object.
     */
    public EventSources buildForClusterHead() {
        return new EventSources().append(localSource()).append(sourceforAddress(withPort(conf.getClusterHeadPort())));
    }


    /**
     * Setup the sources group for worker.
     * 
     * @return an {@link EventSources} object.
     */
    public EventSources buildForWorker() {
        return new EventSources().append(localSource());
    }


    /**
     * @return the localhost source.
     */
    private VismoEventSource localSource() {
        return sourceforAddress(conf.getProducersPoint());
    }


    /**
     * @param address
     * @return the event source for given address
     */
    private VismoEventSource sourceforAddress(final String address) {
        return new VismoEventSource(listener, socketFactory.newBoundPullSocket(address),
                socketFactory.newConnectedPushSocket(address));
    }


    /**
     * @param port
     * @return an address bound to all interfaces and the given port.
     */
    private static String withPort(final int port) {
        return "tcp://127.0.0.1:" + port;
    }
}
