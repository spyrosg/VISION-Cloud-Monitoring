package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.scheduling.VismoRepeatedTask;
import gr.ntua.vision.monitoring.scheduling.VismoTimer;
import gr.ntua.vision.monitoring.udp.UDPListener;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class VismoService extends Thread implements UDPListener {
    /***/
    private static final String            KILL   = "stop!";
    /** the log target. */
    private static final Logger            log    = LoggerFactory.getLogger(VismoService.class);
    /***/
    private static final String            STATUS = "status?";
    /** the list of supporting tasks. */
    private final ArrayList<StoppableTask> tasks  = new ArrayList<StoppableTask>();
    /***/
    private final VismoTimer               timer  = new VismoTimer();


    /**
     * Constructor.
     * 
     * @param elem
     */
    public VismoService(final VismoCloudElement elem) {
        elem.start();
        elem.startTasks(this);
    }


    /**
     * Prepare the task to run.
     * 
     * @param t
     *            the task.
     */
    public void addTask(final StoppableTask t) {
        log.debug("adding slave task {}", t);
        tasks.add(t);
    }


    /**
     * Prepare the task to run.
     * 
     * @param t
     *            the task.
     */
    public void addTimerTask(final VismoRepeatedTask t) {
        log.debug("adding timer task {}", t);
        timer.schedule(t);
    }


    /**
     * @see gr.ntua.vision.monitoring.udp.UDPListener#notify(java.lang.String)
     */
    @Override
    public String notify(final String msg) {
        if (msg.equals(STATUS))
            return status();

        shutDown();
        return KILL;
    }


    /**
     * Start running any supporting tasks.
     */
    @Override
    public void run() {
        log.debug("starting {} task{}", tasks.size(), tasks.size() != 1 ? "s" : "");

        for (final Thread t : tasks)
            t.start();

        log.debug("scheduling {} timer task{}", tasks.size(), tasks.size() != 1 ? "s" : "");
        timer.start();
    }


    /**
     * Stop the application. Wait for the supporting tasks to stop.
     */
    private void shutDown() {
        log.info("shutting down");
        shutdownTasks();
    }


    /**
     * Stop any supporting tasks.
     */
    private void shutdownTasks() {
        for (final StoppableTask t : tasks) {
            log.debug("shutting down slave task {}", t);

            try {
                t.shutDown();
            } catch (final Throwable x) {
                log.error("exception while shutting down", x);
            }
        }

        log.debug("canceling timer");

        try {
            timer.cancel();
        } catch (final Throwable x) {
            log.error("exception while canceling timer", x);
        }

        log.debug("shutdown completed normally.");
    }


    /**
     * @return the pid of the running jvm, as a string.
     */
    @SuppressWarnings("static-method")
    private String status() {
        return String.valueOf(new VismoVMInfo().getPID());
    }
}
