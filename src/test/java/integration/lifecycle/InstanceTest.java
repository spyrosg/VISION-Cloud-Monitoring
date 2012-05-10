package integration.lifecycle;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.CommandClient;
import gr.ntua.vision.monitoring.Config;
import gr.ntua.vision.monitoring.Instance;

import java.io.IOException;
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
public class InstanceTest {
    /***/
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    /***/
    private final Config           cnf       = new Config();
    /***/
    private Instance               inst;


    /**
     * @throws SocketException
     */
    @Before
    public void setUp() throws SocketException {
        inst = new Instance(cnf, null);
    }


    /***/
    @After
    public void tearDown() {
        inst.stop();
    }


    /**
     * @throws IOException
     */
    @Test
    public void whenClientSendsStopCommandServerShouldStop() throws IOException {
        inst.start();

        final CommandClient client = new CommandClient(cnf);

        client.stop();
        exception.expect(SocketTimeoutException.class);
        client.status();
    }


    /**
     * @throws IOException
     */
    @Test
    public void whenStartedClientShouldReportVMPIDfromStatus() throws IOException {
        inst.start();

        final CommandClient client = new CommandClient(cnf);

        assertEquals(cnf.getPID(), client.status());
    }


    /***/
    @Ignore("wip")
    @Test
    public void whenStartedShouldReportEvents() {
        inst.start();
    }
}
