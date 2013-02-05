package endtoend.tests;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.mon.VismoGroupMonitoring;

import java.util.Properties;

import org.junit.Before;


/**
 * TODO
 */
public class VismoGroupMonitoringTest {
    /***/
    @SuppressWarnings("serial")
    private final Properties     props = null;
    /***/
    private VismoConfiguration   conf;
    /***/
    private VismoGroupMonitoring mon;


    /***/
    @Before
    public void setUp() {
        conf = new VismoConfiguration(props);
        mon = new VismoGroupMonitoring(conf);
    }
}
