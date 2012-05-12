package gr.ntua.vision.monitoring;

import java.lang.management.ManagementFactory;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;


/**
 *
 */
public class MonitoringInstance implements UDPListener {
    /***/
    private static final String KILL            = "stop!";
    /** the log target. */
    private static final Logger log             = LoggerFactory.getLogger(MonitoringInstance.class);
    /***/
    private static final String STATUS          = "status?";
    /***/
    private final List<Thread>  tasks           = new ArrayList<Thread>();
    /** the udp port. */
    private final int           UDP_SERVER_PORT = 56431;


    /**
     * Constructor.
     */
    public MonitoringInstance() {
        log.info("Starting up, pid={}, ip={}", getVMPID(), getHostNameIP());
        log.info("running zmq version={}", ZMQ.getVersionString());
    }


    /**
     * @see gr.ntua.vision.monitoring.UDPListener#notify(java.lang.String)
     */
    @Override
    public String notify(final String msg) {
        if (msg.equals(STATUS))
            return status();

        stop();
        return KILL;
    }


    /**
     * Actually start the application. Setup and run any supporting tasks.
     * 
     * @throws SocketException
     */
    public void start() throws SocketException {
        startService(new UDPServer(UDP_SERVER_PORT, this));
    }


    /**
     * Stop the application. Wait for the supporting tasks to stop.
     */
    public void stop() {
        log.info("shutting down");

        for (final Thread t : tasks)
            t.interrupt();

        log.debug("successful shutdown");
    }


    /**
     * Start running the task asynchronously.
     * 
     * @param task
     *            the task to run.
     */
    private void startService(final Thread task) {
        tasks.add(task);
        task.start();
    }


    /**
     * @return the pid of the running jvm, as a string.
     */
    @SuppressWarnings("static-method")
    private String status() {
        return String.valueOf(getVMPID());
    }


    /**
     * @return
     */
    private static String getHostNameIP() {
        return "FIXME";
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
