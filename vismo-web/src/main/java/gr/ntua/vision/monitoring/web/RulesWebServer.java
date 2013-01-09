package gr.ntua.vision.monitoring.web;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;


/**
 * @author tmessini
 */
public class RulesWebServer {
    /***/
    private static final Logger       log        = LoggerFactory.getLogger(RulesWebServer.class);
    /***/
    private final String              BASE_URI;
    /***/
    private final Map<String, String> initParams = new HashMap<String, String>();
    /***/
    private final SelectorThread      selectorThread;
    /***/
    private final int                 serverPort;


    /**
     * we configure the system so as to use the resource package.
     * 
     * @param resourcePackage
     * @param serverPort
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public RulesWebServer(final String resourcePackage, final int serverPort) throws IllegalArgumentException, IOException {
        this.serverPort = serverPort;

        System.out.println("server address:" + getAddress().toString().substring(1));
        BASE_URI = "http://" + getAddress().toString().substring(1) + ":" + serverPort + "/";
        System.out.println("BASE_URI:" + BASE_URI);
        initParams.put("com.sun.jersey.config.property.packages", resourcePackage);
        this.selectorThread = getGrizzly();
    }


    /**
     * @return baseUri
     */
    @SuppressWarnings("unused")
    private static String getBaseURI() {
        return "http://localhost:" + (System.getenv("PORT") != null ? System.getenv("PORT") : "9998") + "/";
    }


    /**
     * @throws IllegalArgumentException
     */
    public void start() throws IllegalArgumentException {
        RulesWebServer.log.info("grizzly started at " + getGrizzlyPort());
    }


    /**
     * @throws IllegalArgumentException
     */
    public void stop() throws IllegalArgumentException {
        selectorThread.stopEndpoint();
        RulesWebServer.log.info("grizzly stopped");

    }


    /**
     * we use this address instead of local host in order grizzly to be accessible with public ipv4
     * 
     * @return InetAddress that the server will be bound.
     */
    private InetAddress getAddress() {
        final NetworkInterface iface;

        try {
            iface = getInterface();
        } catch (final SocketException e) {
            throw new RuntimeException(e);
        }
        final Enumeration<InetAddress> addresses = iface.getInetAddresses();

        while (addresses.hasMoreElements()) {
            final InetAddress addr = addresses.nextElement();

            if (addr instanceof Inet6Address)
                continue;
            return addr;
        }
        return null;
    }


    /**
     * @return selectorThread.
     * @throws IllegalArgumentException
     * @throws IOException
     */
    private SelectorThread getGrizzly() throws IllegalArgumentException, IOException {
        return GrizzlyWebContainerFactory.create(BASE_URI, initParams);
    }


    /**
     * @return Grizzly port
     */
    private int getGrizzlyPort() {

        return serverPort;

    }


    /**
     * Try to get the name of the first public, not loop-back interface that is up on the host machine.
     * 
     * @return the first public interface, or <code>null</code> when no such interface exists.
     * @throws SocketException
     */
    @SuppressWarnings("static-method")
    private NetworkInterface getInterface() throws SocketException {
        final Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();

        while (ifaces.hasMoreElements()) {
            final NetworkInterface iface = ifaces.nextElement();

            if (iface.isLoopback() || !iface.isUp())
                continue;

            final Enumeration<InetAddress> addresses = iface.getInetAddresses();

            while (addresses.hasMoreElements()) {
                final InetAddress addr = addresses.nextElement();

                if (addr instanceof Inet6Address)
                    continue;

                return iface;
            }
        }
        return null;
    }
}
