package unit.tests;

import gr.ntua.vision.monitoring.metrics.HostCPULoad;

import java.io.IOException;

import junit.framework.TestCase;


/**
 *
 */
public class HostCPULoadTest extends TestCase {
    /***/
    private HostCPULoad cpuLoad;


    /**
     * @throws IOException
     */
    public void testShouldGetCPULoad() throws IOException {
        cpuLoad = new HostCPULoad();

        System.err.println(cpuLoad.get());
        assertTrue(cpuLoad.get() > 0);
    }
}
