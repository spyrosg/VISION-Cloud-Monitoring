package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.udp.UDPServer;

import java.net.DatagramSocket;
import java.net.SocketException;

import org.zeromq.ZContext;


/**
 * This is used to configure and properly initialize the application graph.
 */
public class VismoFactory {
    /** the configuration object. */
    private final VismoConfiguration config;


    /**
     * Constructor.
     * 
     * @param config
     *            the configuration object.
     */
    public VismoFactory(final VismoConfiguration config) {
        this.config = config;
    }


    /**
     * Configure and return a {@link Vismo} instance.
     * 
     * @return a new vismo instance.
     * @throws SocketException
     */
    public Vismo build() throws SocketException {
        final Vismo mon = new Vismo(new VismoVMInfo());
        final ZContext ctx = new ZContext();
        final LocalEventsCollector receiver = new LocalEventsCollector(ctx, config.getProducersPoint());

        receiver.subscribe(new EventDistributor(ctx, config.getConsumersPoint()));

        mon.addTask(receiver);
        mon.addTask(new UDPServer(getUDPServeSocket(config.getUDPPort()), mon));

        return mon;
    }


    /**
     * @param port
     * @return
     * @throws SocketException
     */
    public static DatagramSocket getUDPServeSocket(final int port) throws SocketException {
        final DatagramSocket sock = new DatagramSocket(port);

        sock.setReuseAddress(true);

        return sock;
    }
}
