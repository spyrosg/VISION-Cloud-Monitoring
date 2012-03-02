package unit.lifecycle;

import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.InstanceManager;

import java.io.IOException;
import java.net.BindException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
    @Test
    public void cannotStartTwoInstances() throws SocketException {
        final InstanceManager man2 = new InstanceManager();

        man.start();

        try {
            man2.start();
        } catch( final Throwable x ) {
            assertTrue( x.getCause() instanceof BindException );
        } finally {
            man2.stop();
        }
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
     * @throws SocketTimeoutException
     * @throws SocketException
     */
    @Ignore("todo")
    @Test
    public void whenStartedStatusShouldReturnStartedVMPID() throws SocketTimeoutException, SocketException {
        man.start();
        assertTrue( man.status() > 1 );
    }
}
