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
     * @return
     */
    public List< ? extends EventSink> buildForCloudHead() {
        return Arrays.asList(new UniqueEventSink(socketFactory.newPubSocket("tcp://*:" + conf.getConsumersPort())));
    }


    /**
     * @return
     */
    public List< ? extends EventSink> buildForClusterHead() {
        final EventSink sink = new UniqueEventSink(socketFactory.newPubSocket("tcp://*:" + conf.getConsumersPort()));
        final EventSink cloudSink = new UniqueEventSink(socketFactory.newConnectedPushSocket("tcp://"
                + conf.getCloudHeads().get(0) + ":" + conf.getCloudHeadPort()));

        return Arrays.asList(sink, cloudSink);
    }


    /**
     * @return
     */
    public List< ? extends EventSink> buildForWorker() {
        return Arrays.asList(new UniqueEventSink(socketFactory.newConnectedPushSocket("tcp://" + conf.getClusterHead() + ":"
                + conf.getClusterHeadPort())));
    }
}
