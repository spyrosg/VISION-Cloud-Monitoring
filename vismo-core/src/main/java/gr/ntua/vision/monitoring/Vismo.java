package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.scheduling.VismoRepeatedTask;
import gr.ntua.vision.monitoring.scheduling.VismoTimer;
import gr.ntua.vision.monitoring.udp.UDPListener;

import java.net.SocketException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;


/**
 *
 */
public class Vismo implements UDPListener {
    /***/
    private static final String            KILL   = "stop!";
    /** the log target. */
    private static final Logger            log    = LoggerFactory.getLogger(Vismo.class);
    /***/
    private static final String            STATUS = "status?";
    /** the list of supporting tasks. */
    private final ArrayList<StoppableTask> tasks  = new ArrayList<StoppableTask>();
    /***/
    private final VismoTimer               timer  = new VismoTimer();
    /***/
    private final VMInfo                   vminfo;


    /**
     * Constructor.
     * 
     * @param vminfo
     *            the vm info object.
     * @throws SocketException
     */
    Vismo(final VMInfo vminfo) throws SocketException {
        this.vminfo = vminfo;
        log.info("Starting up, pid={}, ip={}", vminfo.getPID(), vminfo.getInterface().getDisplayName() + vminfo.getAddress());
        log.info("running zmq version={}", ZMQ.getVersionString());
    }


    /**
     * Prepare the task to run.
     * 
     * @param t
     *            the task.
     */
    public void addTask(final StoppableTask t) {
        log.debug("adding new vismo slave task {}", t);
        tasks.add(t);
    }


    /**
     * Prepare the task to run.
     * 
     * @param t
     *            the task.
     */
    public void addTimerTask(final VismoRepeatedTask t) {
        log.debug("adding new vismo timer task {}", t);
        timer.schedule(t);
    }


    /**
     * @see gr.ntua.vision.monitoring.udp.UDPListener#notify(java.lang.String)
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
        log.debug("vismo starting {} tasks", tasks.size());

        for (final Thread t : tasks)
            t.start();

        log.debug("vismo scheduling {} timer tasks", tasks.size());
        timer.start();
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
        log.debug("canceling timer");

        try {
            timer.cancel();
        } catch (final Throwable x) {
            log.error("exception while canceling timer", x);
        }

        for (final StoppableTask t : tasks) {
            log.debug("shutting down vismo slave task {}", t);

            try {
                t.shutDown();
            } catch (final Throwable x) {
                log.error("exception while shutting down", x);
            }
        }

        log.debug("shutdown completed normally.");
    }


    /**
     * @return the pid of the running jvm, as a string.
     */
    private String status() {
        return String.valueOf(vminfo.getPID());
    }
}
