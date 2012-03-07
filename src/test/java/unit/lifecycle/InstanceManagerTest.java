package unit.lifecycle;

import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.InstanceManager;

import java.io.IOException;
import java.net.BindException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 */
public class InstanceManagerTest {
    /***/
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    /***/
    private InstanceManager        man;


    /**
     * @throws IOException
     */
    @Test
    public void beforeStartedStatusShouldThrowTimeoutException() throws IOException {
        exception.expect( SocketTimeoutException.class );
        man.status();
    }


    /**
     * @throws SocketException
     */
    @SuppressWarnings("unused")
    @Test
    public void cannotStartTwoInstances() throws SocketException {
        exception.expect( BindException.class );
        new InstanceManager();
    }


    /**
     * @throws SocketException
     */
    @Before
    public void setUp() throws SocketException {
        man = new InstanceManager();
    }


    /**
     * 
     */
    @After
    public void tearDown() {
        man.stop();
    }


    /**
     * @throws IOException
     */
    @Test
    public void whenStartedStatusShouldReturnStartedVMPID() throws IOException {
        man.start();
        assertTrue( man.status() > 1 );
    }
}
