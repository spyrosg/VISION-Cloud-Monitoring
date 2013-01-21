package gr.ntua.vision.monitoring.threading;

import java.util.Timer;
import java.util.TimerTask;


/**
 * This task represents calculations that happen periodically.
 */
public abstract class PeriodicTask extends TimerTask {
    /***/
    private final long period;


    /**
     * Constructor.
     * 
     * @param period
     */
    public PeriodicTask(final long period) {
        this.period = period;
    }


    /**
     * Schedule the task to be run under the timer.
     * 
     * @param timer
     *            the timer.
     */
    public void scheduleWith(final Timer timer) {
        timer.schedule(this, 0, period);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "<" + getClass() + ", running every " + period / 1000 + " seconds>";
    }
}
