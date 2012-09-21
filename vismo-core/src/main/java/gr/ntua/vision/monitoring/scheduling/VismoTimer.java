package gr.ntua.vision.monitoring.scheduling;

import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class VismoTimer {
    /***/
    private static final long                  DELAY = TimeUnit.SECONDS.toMillis(5);
    /***/
    private static final Logger                log   = LoggerFactory.getLogger(VismoTimer.class);
    /***/
    private final ArrayList<VismoRepeatedTask> tasks = new ArrayList<VismoRepeatedTask>();
    /** the actual timer. */
    private final Timer                        timer = new Timer();


    /**
     * 
     */
    public void cancel() {
        log.debug("canceling timer tasks.");
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
        log.debug("scheduling {} tasks", tasks.size());

        for (final VismoRepeatedTask t : tasks)
            timer.schedule(t, DELAY, t.getPeriod());

        tasks.clear();
    }
}
