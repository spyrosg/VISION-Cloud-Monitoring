package gr.ntua.vision.monitoring.scheduling;

import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.TimeUnit;


/**
 * 
 */
public class VismoTimer {
    /***/
    private static final long                  DELAY = TimeUnit.SECONDS.toMillis(5);
    /***/
    private final ArrayList<VismoRepeatedTask> tasks = new ArrayList<VismoRepeatedTask>();
    /** the actual timer. */
    private final Timer                        timer = new Timer();


    /**
     * 
     */
    public void cancel() {
        timer.cancel();
    }


    /**
     * @param t
     */
    public void schedule(final VismoRepeatedTask t) {
        tasks.add(t);
    }


    /**
     * 
     */
    public void start() {
        for (final VismoRepeatedTask t : tasks)
            timer.schedule(t, DELAY, t.getPeriod());

        tasks.clear();
    }
}
