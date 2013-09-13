package gr.ntua.vision.monitoring;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * VM information provided by the vismo instance.
 */
public class VismoVMInfo implements VMInfo {
    /***/
    private static final InetAddress addr;
    /***/
    private static final Logger      log = Logger.getLogger(VismoVMInfo.class.getName());

    static {
        activateLogger();
        addr = getAddress1();
    }


    /**
     * @see gr.ntua.vision.monitoring.VMInfo#getAddress()
     */
    @Override
    public InetAddress getAddress() {
        return addr;
    }


    /**
     * @see gr.ntua.vision.monitoring.VMInfo#getPID()
     */
    @Override
    public int getPID() {
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf("@");

        if (index < 0)
            throw new Error("Cannot get the pid of this jvm");

        return Integer.parseInt(jvmName.substring(0, index));
    }


    /**
     * @see gr.ntua.vision.monitoring.VMInfo#getVersion()
     */
    @Override
    public String getVersion() {
        final Properties p = new Properties();

        try {
            p.load(VismoVMInfo.class.getResourceAsStream("/git.properties"));
        } catch (final IOException e) {
            throw new Error("cannot get system version");
        }

        return p.getProperty("git.commit.id.abbrev") + " built on " + p.getProperty("git.build.time");
    }


    /**
     * @see gr.ntua.vision.monitoring.VMInfo#isHostAddress(java.lang.String)
     */
    @Override
    public boolean isHostAddress(final String ip) {
        try {
            return isHostAddress1(ip);
        } catch (final SocketException e) {
            log.warning(ip + " => " + e);
            return false;
        }
    }


    /***/
    private static void activateLogger() {
        final ConsoleHandler h = new ConsoleHandler();
        final String pkg = VismoVMInfo.class.getPackage().getName();

        h.setFormatter(new VismoFormatter());
        h.setLevel(Level.ALL);
        Logger.getLogger(pkg).addHandler(h);
        Logger.getLogger(pkg).setLevel(Level.ALL);
    }


    /**
     * @return an inet address.
     */
    private static InetAddress getAddress1() {
        final NetworkInterface iface;

        try {
            iface = getInterface();
            log.config("selected " + iface + " interface");
        } catch (final SocketException e) {
            throw new RuntimeException(e);
        }

        final Enumeration<InetAddress> addresses = iface.getInetAddresses();

        while (addresses.hasMoreElements()) {
            final InetAddress a = addresses.nextElement();

            if (a instanceof Inet6Address)
                continue;

            log.config("selected " + a + " address");

            return a;
        }

        return null;
    }


    /**
     * Try to get the name of the first public, not loop-back interface that is up on the host machine.
     * 
     * @return the first public interface, or <code>null</code> when no such interface exists.
     * @throws SocketException
     */
    private static NetworkInterface getInterface() throws SocketException {
        final Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();

        while (ifaces.hasMoreElements()) {
            final NetworkInterface iface = ifaces.nextElement();

            if (iface.isLoopback() || !iface.isUp())
                continue;

            final Enumeration<InetAddress> addresses = iface.getInetAddresses();

            while (addresses.hasMoreElements()) {
                final InetAddress a = addresses.nextElement();

                if (a instanceof Inet6Address)
                    continue;

                return iface;
            }
        }

        return null;
    }


    /**
     * @param ip
     * @return true when the given address is assigned to the host machine, false otherwise.
     * @see #isHostAddress(String)
     * @throws SocketException
     */
    private static boolean isHostAddress1(final String ip) throws SocketException {
        final Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();

        while (ifaces.hasMoreElements()) {
            final NetworkInterface iface = ifaces.nextElement();

            if (iface.isLoopback() || !iface.isUp())
                continue;

            final Enumeration<InetAddress> addresses = iface.getInetAddresses();

            while (addresses.hasMoreElements()) {
                final InetAddress a = addresses.nextElement();

                if (a instanceof Inet6Address)
                    continue;

                if (ip.equals(a.getHostAddress()))
                    return true;
            }
        }

        return false;
    }
}
