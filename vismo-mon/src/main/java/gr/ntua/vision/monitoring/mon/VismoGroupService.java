package gr.ntua.vision.monitoring.mon;

import java.util.Timer;
import java.util.TimerTask;


/**
 * A shell object to keep the various threads running.
 */
public class VismoGroupService {
    /** the thread to start. */
    private final Thread    t;
    /** the task to schedule. */
    private final TimerTask task;
    /** the timer object. */
    private final Timer     timer = new Timer(true);


    /**
     * Constructor.
     * 
     * @param t
     * @param task
     */
    public VismoGroupService(final Thread t, final TimerTask task) {
        this.t = t;
        this.task = task;
    }


    /**
     * Start the service.
     */
    public void start() {
        timer.schedule(task, 2 * 1000, 10 * 1000);
        t.start();
    }
}
