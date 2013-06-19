package integration.tests;

import gr.ntua.vision.monitoring.resources.CPUUsageBean;
import gr.ntua.vision.monitoring.resources.InternalMetricsResource;
import gr.ntua.vision.monitoring.resources.MemoryUsageBean;
import gr.ntua.vision.monitoring.web.WebAppBuilder;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.ClientResponse;


/**
 * 
 */
public class InternalMetricsTest extends JerseyResourceTest {
    /**
     * @see integration.tests.JerseyResourceTest#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        final Application app = WebAppBuilder.buildFrom(new InternalMetricsResource());

        configureServer(app, "/*");
        startServer();
    }


    /**
     * 
     */
    public void testShouldReturnHostAverageCPULoad() {
        final ClientResponse res = root().path("mon").path("cpu").accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());

        final CPUUsageBean u = res.getEntity(CPUUsageBean.class);
        final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        assertEquals(osBean.getSystemLoadAverage(), u.getHostCPULoad(), 0.5);
    }


    /***/
    public void testShouldReturnProcessMemoryUsage() {
        final ClientResponse res = root().path("mon").path("mem").accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);

        assertEquals(ClientResponse.Status.OK, res.getClientResponseStatus());

        final MemoryUsageBean u = res.getEntity(MemoryUsageBean.class);

        assertTrue(u.getFreeMemoryInBytes() > 0);
    }
}
