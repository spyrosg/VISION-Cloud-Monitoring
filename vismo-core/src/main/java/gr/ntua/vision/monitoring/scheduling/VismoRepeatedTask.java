package gr.ntua.vision.monitoring.scheduling;

import java.util.TimerTask;


/**
 * This task represents calculations that happen repeatedly.
 */
public abstract class VismoRepeatedTask extends TimerTask {
    /**
     * @return the execution period of the task in milliseconds.
     */
    public abstract long getPeriod();
}
