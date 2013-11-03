package unit.tests;

import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.metrics.HostMemory;
import gr.ntua.vision.monitoring.metrics.HostMemoryMetric;
import gr.ntua.vision.monitoring.metrics.LinuxHostMemoryMetric;
import gr.ntua.vision.monitoring.metrics.MacOSMemoryMetric;

import java.io.IOException;

import junit.framework.TestCase;


/**
 *
 */
public class HostMemoryMetricTest extends TestCase {
    /***/
    private HostMemoryMetric  memoryUsage;
    /***/
    private final VismoVMInfo vm = new VismoVMInfo();


    /**
     * @throws IOException
     * @throws InterruptedException
     */
    public void testShouldReportMemoryUsage() throws IOException, InterruptedException {
        memoryUsage = getHostMemoryUsage();

        final HostMemory mem = memoryUsage.get();

        System.err.println("this host (" + vm.getAddress().getHostAddress() + ") is utilizing " + mem.used + " out of "
                + mem.total + " bytes");
        assertTrue("expecting memory usage of host to be greater than zero", mem.used > 0);
    }


    /**
     * @return a HostMemoryMetric object.
     */
    private static HostMemoryMetric getHostMemoryUsage() {
        if (System.getProperty("os.name").toLowerCase().contains("linux"))
            return new LinuxHostMemoryMetric();

        return new MacOSMemoryMetric();
    }
}
