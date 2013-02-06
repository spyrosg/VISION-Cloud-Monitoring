package gr.ntua.vision.monitoring.mon;

import java.util.Timer;
import java.util.TimerTask;


/**
 *
 */
public class VismoGroupMonitoring {
    /***/
    private static final long      TWO_SECONDS = 2 * 1000;
    /***/
    private final VismoGroupServer server;
    /***/
    private final Timer            timer       = new Timer(true);


    /**
     * Constructor.
     * 
     * @param server
     */
    public VismoGroupMonitoring(final VismoGroupServer server) {
        this.server = server;
    }


    /**
     * @param period
     * @param task
     */
    public void addTask(final long period, final TimerTask task) {
        timer.schedule(task, TWO_SECONDS, period);
    }


    /**
     * @param listener
     */
    public void register(final GroupNotification listener) {
        server.register(listener);
    }


    /**
     * Start the service.
     */
    public void start() {
        new Thread(server, "vismo-group-server-monitoring").start();
    }
}
