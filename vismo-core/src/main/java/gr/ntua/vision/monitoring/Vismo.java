package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.udp.UDPListener;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;


/**
 *
 */
public class Vismo implements UDPListener {
    /***/
    private static final long                          DELAY      = TimeUnit.SECONDS.toMillis(10);
    /***/
    private static final String                        KILL       = "stop!";
    /** the log target. */
    private static final Logger                        log        = LoggerFactory.getLogger(Vismo.class);
    /***/
    private static final String                        STATUS     = "status?";
    /** the list of supporting tasks. */
    private final ArrayList<StoppableTask>             tasks      = new ArrayList<StoppableTask>();
    /***/
    private final Timer                                timer      = new Timer();
    /***/
    private final ArrayList<VismoAggregationTimerTask> timerTasks = new ArrayList<VismoAggregationTimerTask>();
    /***/
    private final VMInfo                               vminfo;


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
    public void addTimerTask(final VismoAggregationTimerTask t) {
        log.debug("adding new vismo timer task {}", t);
        timerTasks.add(t);
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

        for (final VismoAggregationTimerTask t : timerTasks)
            timer.schedule(t, DELAY, t.getPeriod());
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
