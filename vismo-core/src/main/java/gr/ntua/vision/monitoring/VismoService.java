package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.scheduling.VismoPeriodicTask;
import gr.ntua.vision.monitoring.udp.UDPListener;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;


/**
 * This is responsible for starting/stopping the vismo instance. It maintains the set of application's running threads.
 */
public class VismoService implements UDPListener {
    /***/
    private static final String            KILL   = "stop!";
    /***/
    private static final Logger            log    = LoggerFactory.getLogger(VismoService.class);
    /***/
    private static final String            STATUS = "status?";
    /***/
    private final ArrayList<StoppableTask> tasks  = new ArrayList<StoppableTask>();
    /***/
    private final Timer                    timer  = new Timer();
    /***/
    private final VMInfo                   vminfo;


    /**
     * Constructor.
     * 
     * @param vminfo
     * @throws SocketException
     */
    public VismoService(final VMInfo vminfo) throws SocketException {
        log.info("Starting up, pid={}, ip={}", vminfo.getPID(), vminfo.getInterface().getDisplayName() + vminfo.getAddress());
        log.info("running zmq version={}", ZMQ.getVersionString());
        this.vminfo = vminfo;
    }


    /**
     * Add a task owned by <code>this</code> service. The task will start executing upon {@link #start()}.
     * 
     * @param task
     *            the task.
     */
    public void addTask(final StoppableTask task) {
        tasks.add(task);
    }


    /**
     * Schedule a periodic task. The task will start executing immediately.
     * 
     * @param task
     *            the task.
     */
    public void addTask(final VismoPeriodicTask task) {
        task.scheduleWith(timer);
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
     * Actually start the application.
     * 
     * @return <code>this</code>.
     */
    public VismoService start() {
        log.debug("starting {} task{}", tasks.size(), tasks.size() != 1 ? "s" : "");

        for (final StoppableTask task : tasks) {
            log.debug("\t{}", task);
            task.start();
        }

        return this;
    }


    /**
     * @return the jvm's pid.
     */
    public String status() {
        return String.valueOf(vminfo.getPID());
    }


    /**
     * Stop the application.
     */
    public void stop() {
        log.info("shutting down");

        timer.cancel();

        for (final StoppableTask task : tasks)
            shutDownTask(task);

        log.debug("shutdown completed normally.");
    }


    /**
     * Kill the task.
     * 
     * @param task
     *            the task.
     */
    private static void shutDownTask(final StoppableTask task) {
        try {
            task.halt();
        } catch (final Throwable x) {
            log.error("trying to shutdown {}", task);
            log.error("error", x);
        }
    }
}
