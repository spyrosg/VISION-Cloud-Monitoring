package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.udp.UDPFactory;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;


/**
 * This is used to configure and properly initialize the application graph.
 */
public class VismoFactory {
    /** the log target. */
    private static final Logger      log = LoggerFactory.getLogger(VismoFactory.class);
    /** the configuration object. */
    private final VismoConfiguration conf;


    /**
     * Constructor.
     * 
     * @param conf
     *            the configuration object.
     */
    public VismoFactory(final VismoConfiguration conf) {
        this.conf = conf;
    }


    /**
     * Configure and return a {@link Vismo} instance.
     * 
     * @param listeners
     * @return a new vismo instance.
     * @throws SocketException
     */
    public Vismo build(final EventListener... listeners) throws SocketException {
        final ZMQSockets zmq = new ZMQSockets(new ZContext());
        final LocalEventsCollector receiver = new LocalEventsCollectorFactory(conf).build(zmq);
        final Vismo vismo = new Vismo(new VismoVMInfo());

        log.debug("new zsocket for {} bound to={}", EventDistributor.class, conf.getConsumersPoint());
        receiver.subscribe(new EventDistributor(zmq.newBoundPubSocket(conf.getConsumersPoint())));

        for (final EventListener listener : listeners)
            receiver.subscribe(listener);

        vismo.addTask(receiver);
        vismo.addTask(new UDPFactory(conf.getUDPPort()).buildServer(vismo));

        return vismo;
    }
}
