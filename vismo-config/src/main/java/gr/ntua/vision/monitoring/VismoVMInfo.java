package gr.ntua.vision.monitoring;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Properties;


/**
 * VM information provided by the vismo instance.
 */
public class VismoVMInfo implements VMInfo {
    /**
     * @see gr.ntua.vision.monitoring.VMInfo#getAddress()
     */
    @Override
    public InetAddress getAddress() {
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
                final InetAddress addr = addresses.nextElement();

                if (addr instanceof Inet6Address)
                    continue;

                return iface;
            }
        }

        return null;
    }
}
