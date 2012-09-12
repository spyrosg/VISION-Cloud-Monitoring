package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.scheduling.VismoRepeatedTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class JVMStatusReportTask extends VismoRepeatedTask {
    /***/
    private static final Logger log = LoggerFactory.getLogger(JVMStatusReportTask.class);
    /***/
    private static final int    MB  = 1024 * 1024;
    /***/
    private final long          period;


    /**
     * Constructor.
     * 
     * @param period
     */
    public JVMStatusReportTask(final long period) {
        this.period = period;
    }


    /**
     * @see gr.ntua.vision.monitoring.scheduling.VismoRepeatedTask#getPeriod()
     */
    @Override
    public long getPeriod() {
        return period;
    }


    /**
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        reportFreeMemoryPercent();
    }


    /**
     * 
     */
    private static void reportFreeMemoryPercent() {
        final long freeMemBytes = Runtime.getRuntime().freeMemory();
        final long totalMemBytes = Runtime.getRuntime().totalMemory();
        final double used = 100.0 * (totalMemBytes - freeMemBytes) / totalMemBytes;
        final long totalMemMBytes = totalMemBytes / MB;

        log.trace("memory: using {}% of {} MBytes", String.format("%.3g", used), totalMemMBytes);
    }
}
