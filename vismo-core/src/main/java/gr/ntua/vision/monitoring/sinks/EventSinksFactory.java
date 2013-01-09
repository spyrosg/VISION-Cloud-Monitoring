package gr.ntua.vision.monitoring.sinks;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import org.zeromq.ZContext;


/**
 * Configure and provide the system with the appropriate event sinks.
 */
public class EventSinksFactory {
    /** the configuration object. */
    private final VismoConfiguration conf;
    /***/
    private final ZMQFactory         socketFactory;


    /**
     * Constructor.
     * 
     * @param conf
     *            the configuration object.
     */
    public EventSinksFactory(final VismoConfiguration conf) {
        this(conf, new ZMQFactory(new ZContext()));
    }


    /**
     * Constructor.
     * 
     * @param conf
     *            the configuration object.
     * @param socketFactory
     */
    public EventSinksFactory(final VismoConfiguration conf, final ZMQFactory socketFactory) {
        this.conf = conf;
        this.socketFactory = socketFactory;
    }


    /**
     * @return an {@link EventSinks} object.
     */
    public EventSinks buildForCloudHead() {
        return new EventSinks(new UniqueEventSink(socketFactory.newBoundPubSocket("tcp://*:" + conf.getConsumersPort())));
    }


    /**
     * Setup the sinks for a cluster head node.
     * 
     * @return an {@link EventSinks} object.
     */
    public EventSinks buildForClusterHead() {
        final EventSink sink = new UniqueEventSink(socketFactory.newBoundPubSocket("tcp://*:" + conf.getConsumersPort()));
        final EventSink cloudSink = new UniqueEventSink(socketFactory.newConnectedPushSocket("tcp://"
                + conf.getCloudHeads().get(0) + ":" + conf.getCloudHeadPort()));

        return new EventSinks(sink, cloudSink);
    }


    /**
     * Setup the sinks for a worker node.
     * 
     * @return an {@link EventSinks} object.
     */
    public EventSinks buildForWorker() {
        return new EventSinks(new UniqueEventSink(socketFactory.newConnectedPushSocket("tcp://" + conf.getClusterHead() + ":"
                + conf.getClusterHeadPort())));
    }
}
