package gr.ntua.vision.monitoring.threading;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.management.OperatingSystemMXBean;


/**
 * This is used to calculate and report the memory and cpu usage of this process.
 */
@SuppressWarnings("restriction")
public class JVMStatusReportTask extends PeriodicTask {
    /** the log target. */
    private static final Logger                log      = LoggerFactory.getLogger(JVMStatusReportTask.class);
    /** the system's number of cpus. */
    private static final int                   nCPUs;
    /***/
    private static final OperatingSystemMXBean osBean   = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                                                                .getOperatingSystemMXBean();
    /** the process' time in the cpu(s), in milliseconds. */
    private static double                      procTime = getProcCPUTime();
    /***/
    private static final RuntimeMXBean         runbean  = ManagementFactory.getRuntimeMXBean();
    /** the runtime object. */
    private static final Runtime               runtime  = Runtime.getRuntime();
    /** the process' uptime, in milliseconds. */
    private static double                      upTime   = getProcUpTime();

    static {
        nCPUs = runtime.availableProcessors();
    }


    /**
     * Constructor.
     * 
     * @param period
     *            the period's task.
     */
    public JVMStatusReportTask(final long period) {
        super(period);
    }


    /**
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        log.debug("cpu load {}%", String.format("%.2f", getCPULoad()));
        log.debug("total memory {} Mbytes, used {}%", runtime.totalMemory() / (1024 * 1024),
                  String.format("%.2f", getUsedMemory()));
    }


    /**
     * Calculate something like the vm's process load on the cpu. This is the ratio of elapsed cpu time spent by the process to
     * the elapsed uptime of the process.
     * 
     * @return the vm's cpu load as a double in the range [0, 1).
     */
    public static double getCPULoad() {
        final double elapsedUpTime = getProcUpTime() - upTime;
        final double elapsedProcTime = getProcCPUTime() - procTime;

        // cpu load could go higher than 100% because currUpTime
        // and currProcTime are not fetched simultaneously. Limit it to 99%.
        return Math.min(99.9, 100 * elapsedProcTime / (nCPUs * elapsedUpTime));
    }


    /**
     * @return the percent of used memory in the jvm.
     */
    public static double getUsedMemory() {
        final long freeBytes = runtime.freeMemory();
        final double totalBytes = runtime.totalMemory();

        return 100 * (totalBytes - freeBytes) / totalBytes;
    }


    /**
     * @return the time this process has spent in the cpu(s) in milliseconds.
     */
    private static double getProcCPUTime() {
        return osBean.getProcessCpuTime() / 1000000.0;
    }


    /**
     * @return the time this process has been running in milliseconds.
     */
    private static double getProcUpTime() {
        return runbean.getUptime();
    }
}
