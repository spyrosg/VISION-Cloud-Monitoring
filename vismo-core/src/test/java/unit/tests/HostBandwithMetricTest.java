package unit.tests;

import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.metrics.HostBandwithMetric;
import gr.ntua.vision.monitoring.metrics.HostBandwithMetric.Bandwidth;

import java.io.IOException;

import junit.framework.TestCase;


/**
 *
 */
public class HostBandwithMetricTest extends TestCase {
    /***/
    private HostBandwithMetric bandwithUsage;
    /***/
    private final VismoVMInfo  vm = new VismoVMInfo();


    /**
     * @throws IOException
     * @throws InterruptedException
     */
    public void testShouldReportInboundAndOutBoundUsage() throws IOException, InterruptedException {
        bandwithUsage = new HostBandwithMetric();

        final Bandwidth bw = bandwithUsage.get();

        System.err.println("this host (" + vm.getAddress().getHostAddress() + ") is seeing" + bw.inbound + " bytes inbound and "
                + bw.outbound + " bytes outbound");
        assertTrue("expecting memory usage of host to be greater than zero", bw.inbound > 0 && bw.outbound > 0);
    }

}
