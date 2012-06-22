package unit;

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


    /**
     * @throws Exception
     */
    @Test
    public void isValidIPAddress() throws Exception {
        assertNotNull(vminfo.getAddress());
    }


    /**
     * @throws Exception
     */
    @Test
    public void looksLikePID() throws Exception {
        assertTrue(vminfo.getPID() > 1);
    }
}
