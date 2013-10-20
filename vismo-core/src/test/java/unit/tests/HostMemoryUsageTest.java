package unit.tests;

import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.metrics.HostMemory;
import gr.ntua.vision.monitoring.metrics.HostMemoryUsage;
import gr.ntua.vision.monitoring.metrics.LinuxHostMemoryUsage;
import gr.ntua.vision.monitoring.metrics.MacOSMemoryUsage;

import java.io.IOException;

import junit.framework.TestCase;


/**
 *
 */
public class HostMemoryUsageTest extends TestCase {
    /***/
    private HostMemoryUsage   memoryUsage;
    /***/
    private final VismoVMInfo vm = new VismoVMInfo();


    /**
     * @throws IOException
     * @throws InterruptedException
     */
    public void testShouldGetCPULoad() throws IOException, InterruptedException {
        memoryUsage = getHostMemoryUsage();

        final HostMemory mem = memoryUsage.get();

        System.err.println("this host (" + vm.getAddress().getHostAddress() + ") is utilizing " + mem.used + " out of "
                + mem.total + " bytes");
        assertTrue("expecting memory usage of host to be greater than zero", mem.used > 0);
    }


    /**
     * @return a HostMemoryUsage object.
     */
    private static HostMemoryUsage getHostMemoryUsage() {
        if (System.getProperty("os.name").toLowerCase().contains("linux"))
            return new LinuxHostMemoryUsage();

        return new MacOSMemoryUsage();
    }
}
