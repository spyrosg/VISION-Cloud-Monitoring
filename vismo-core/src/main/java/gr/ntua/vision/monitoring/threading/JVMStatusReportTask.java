package gr.ntua.vision.monitoring.threading;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.management.OperatingSystemMXBean;


/**
 * 
 */
@SuppressWarnings("restriction")
public class JVMStatusReportTask extends VismoPeriodicTask {
    /***/
    private static final Logger                log         = LoggerFactory.getLogger(JVMStatusReportTask.class);
    /** the number of bytes in a megabyte. */
    private static final int                   MB          = 1024 * 1024;
    /** a millisecond in nanoseconds. */
    private static final int                   MILLI       = 1000000;
    /** the system's number of cpus. */
    private static final int                   nCPUs;
    /***/
    private static final OperatingSystemMXBean osBean      = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                                                                   .getOperatingSystemMXBean();
    /***/
    private static final RuntimeMXBean         runbean     = ManagementFactory.getRuntimeMXBean();
    /***/
    private static final Runtime               runtime     = Runtime.getRuntime();
    /** the task's running period. */
    private final long                         period;
    /** the vm's process time in milliseconds on latest access. */
    private long                               processTime = 0;
    /** the vm's uptime in milliseconds on latest access. */
    private long                               upTime      = 0;

    static {
        nCPUs = osBean.getAvailableProcessors();
    }


    /**
     * Constructor.
     * 
     * @param period
     */
    public JVMStatusReportTask(final long period) {
        this.period = period;
    }


    /**
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        reportFreeMemoryPercent();
        reportCPUUsage();
    }


    /**
     * @see gr.ntua.vision.monitoring.threading.VismoPeriodicTask#scheduleWith(java.util.Timer)
     */
    @Override
    public void scheduleWith(final Timer timer) {
        timer.schedule(this, 0, period);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<JVMStatusReportTask, running every " + period / 1000 + " seconds>";
    }


    /**
     * Calculate something like the vm's load on the cpu. This is the amount time spent using the cpu over the time difference of
     * two consecutive calls to this method. <br />
     * 
     * @See {@linkplain "http://knight76.blogspot.gr/2009/05/how-to-get-java-cpu-usage-jvm-instance.html"}
     * @return the vm's cpu load as a double in the range [0, 1).
     */
    private double getVMCPULoad() {
        final long currUpTime = runbean.getUptime();
        final long currProcessTime = osBean.getProcessCpuTime() / MILLI;

        if (upTime <= 0 || processTime <= 0) {
            // swap out the old values
            upTime = currUpTime;
            processTime = currProcessTime;

            return 0.0;
        }

        // cpu load could go higher than 100% because currUpTime
        // and currProcessTime are not fetched simultaneously. Limit to 99%
        final double load = Math.min(99.0, 1.0 * (currProcessTime - processTime) / (nCPUs * (currUpTime - upTime)));

        // swap out the old values
        upTime = currUpTime;
        processTime = currProcessTime;

        return load;
    }


    /**
     * 
     */
    private void reportCPUUsage() {
        log.debug("cpu load {}%", String.format("%.2f", getVMCPULoad()));
    }


    /**
     * 
     */
    private static void reportFreeMemoryPercent() {
        final long freeMemBytes = runtime.freeMemory();
        final long totalMemBytes = runtime.totalMemory();
        final double used = 100.0 * (totalMemBytes - freeMemBytes) / totalMemBytes;
        final long totalMemMBytes = totalMemBytes / MB;

        log.debug("using {}% of {} MBytes", String.format("%.2f", used), totalMemMBytes);
    }
}
