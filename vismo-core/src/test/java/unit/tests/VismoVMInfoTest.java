package unit.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.VMInfo;
import gr.ntua.vision.monitoring.VismoVMInfo;

import org.junit.Test;


/**
 * Basic assertions for {@link VismoVMInfo}.
 */
public class VismoVMInfoTest {
    /** the object under test. */
    private final VMInfo vminfo = new VismoVMInfo();


    /***/
    @Test
    public void isValidIPAddress() {
        assertNotNull(vminfo.getAddress());
    }


    /***/
    @Test
    public void looksLikePID() {
        assertTrue(vminfo.getPID() > 1);
    }
}
