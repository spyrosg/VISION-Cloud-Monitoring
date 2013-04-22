package gr.ntua.vision.monitoring.sinks;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.util.Arrays;
import java.util.List;

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
     * @return the list of {@link EventSink}s for the cloud head service.
     */
    public List< ? extends EventSink> buildForCloudHead() {
        final String consumersAddress = "tcp://*:" + conf.getConsumersPort();

        return Arrays.asList(new TopicedEventSink(socketFactory.newPubSocket(consumersAddress)));
    }


    /**
     * @return the list of {@link EventSink}s for the cluster head service.
     */
    public List< ? extends EventSink> buildForClusterHead() {
        final String consumersAddress = "tcp://*:" + conf.getConsumersPort();
        final EventSink sink = new TopicedEventSink(socketFactory.newPubSocket(consumersAddress));

        final String cloudHeadAddress = "tcp://" + conf.getCloudHeads().get(0) + ":" + conf.getCloudHeadPort();
        final EventSink cloudSink = new PlainEventSink(socketFactory.newConnectedPushSocket(cloudHeadAddress));

        return Arrays.asList(sink, cloudSink);
    }


    /**
     * @return the list of {@link EventSink}s for the worker service.
     */
    public List< ? extends EventSink> buildForWorker() {
        final String clusterHeadAddress = "tcp://" + conf.getClusterHead() + ":" + conf.getClusterHeadPort();

        return Arrays.asList(new PlainEventSink(socketFactory.newConnectedPushSocket(clusterHeadAddress)));
    }
}
