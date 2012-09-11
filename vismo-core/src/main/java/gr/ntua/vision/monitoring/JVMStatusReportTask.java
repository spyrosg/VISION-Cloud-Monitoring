package gr.ntua.vision.monitoring;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class JVMStatusReportTask extends TimerTask {
    /***/
    private static final Logger log = LoggerFactory.getLogger(JVMStatusReportTask.class);
    /***/
    private static final int    MB  = 1024 * 1024;


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
