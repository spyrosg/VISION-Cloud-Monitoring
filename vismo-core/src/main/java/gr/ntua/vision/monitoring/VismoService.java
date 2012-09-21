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
 * This is responsible for starting/stopping the vismo instance. It maintains a list of the application running threads.
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
    private final VismoTimer               timer  = new VismoTimer();
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
     * @param task
     */
    public void addTask(final StoppableTask task) {
        tasks.add(task);
    }


    /**
     * @param task
     */
    public void addTask(final VismoRepeatedTask task) {
        timer.schedule(task);
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
     * 
     */
    public void start() {
        log.debug("starting {} tasks", tasks.size());

        for (final StoppableTask task : tasks)
            task.start();

        timer.start();
    }


    /**
     * @return the jvm's pid.
     */
    private String status() {
        return String.valueOf(vminfo.getPID());
    }


    /**
     * 
     */
    private void stop() {
        log.info("shutting down");

        timer.cancel();

        log.info("stopping tasks");

        for (final StoppableTask task : tasks)
            try {
                task.shutDown();
            } catch (final Throwable x) {
                log.error("trying to shutdown {}", task);
                log.error("error", x);
            }

        log.debug("shutdown completed normally.");
    }
}
