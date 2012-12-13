package gr.ntua.vision.monitoring.threading;

import java.util.Timer;
import java.util.TimerTask;


/**
 * This task represents calculations that happen periodically.
 */
public abstract class PeriodicTask extends TimerTask {
    /**
     * Schedule the task to be run under the timer.
     * 
     * @param timer
     *            the timer.
     */
    public abstract void scheduleWith(final Timer timer);
}
