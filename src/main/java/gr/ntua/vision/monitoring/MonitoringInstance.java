package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.UDPServer.UDPListener;

import java.lang.management.ManagementFactory;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;


/**
 *
 */
public class MonitoringInstance implements UDPListener {
    /***/
    private static final String       KILL   = "stop!";
    /** the log target. */
    private static final Logger       log    = LoggerFactory.getLogger(MonitoringInstance.class);
    /***/
    private static final String       STATUS = "status?";
    /** the list of supporting tasks. */
    private final List<StoppableTask> tasks  = new ArrayList<StoppableTask>();


    /**
     * Constructor.
     */
    public MonitoringInstance() {
        log.info("Starting up, pid={}, ip={}", getVMPID(), getInterfaceIP());
        log.info("running zmq version={}", ZMQ.getVersionString());
    }


    /**
     * Prepare the task to run.
     * 
     * @param t
     */
    public void addTask(final StoppableTask t) {
        tasks.add(t);
    }


    /**
     * @see gr.ntua.vision.monitoring.UDPServer.UDPListener#notify(java.lang.String)
     */
    @Override
    public String notify(final String msg) {
        if (msg.equals(STATUS))
            return status();

        stop();
        return KILL;
    }


    /**
     * Start running any supporting tasks.
     */
    public void start() {
        for (final Thread t : tasks)
            t.start();
    }


    /**
     * Stop the application. Wait for the supporting tasks to stop.
     */
    public void stop() {
        log.info("shutting down");
        shutdownTasks();
    }


    /**
     * Stop any supporting tasks.
     */
    private void shutdownTasks() {
        for (final StoppableTask t : tasks)
            t.shutDown();
    }


    /**
     * @return the pid of the running jvm, as a string.
     */
    @SuppressWarnings("static-method")
    private String status() {
        return String.valueOf(getVMPID());
    }


    /**
     * Try to get the name of the first public, not loop-back interface that is up on the host machine, plus, the first non inet6
     * address of that interface.
     * 
     * @return on success, the name of the interface and the inet address, separated by a slash, <code>null</code> otherwise.
     */
    private static String getInterfaceIP() {
        try {
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

                    return iface.getDisplayName() + "/" + addr.getHostAddress();
                }
            }
        } catch (final SocketException e) {
            // ignore
        }

        return null;
    }


    /**
     * @return the pid of the running jvm.
     * @throws Error
     *             when the pid is not available for this jvm.
     */
    private static int getVMPID() {
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf("@");

        if (index < 0)
            throw new Error("Cannot get the pid of this jvm");

        return Integer.parseInt(jvmName.substring(0, index));
    }
}
