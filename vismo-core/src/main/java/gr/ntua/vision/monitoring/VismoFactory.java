package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.udp.UDPFactory;

import java.net.SocketException;


/**
 * This is used to configure and properly initialize the application graph.
 */
public class VismoFactory {
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
     * @return a new vismo instance.
     * @throws SocketException
     */
    public Vismo build() throws SocketException {
        final Vismo mon = new Vismo(new VismoVMInfo());

        mon.addTask(new UDPFactory(conf.getUDPPort()).buildServer(mon));

        return mon;
    }
}
