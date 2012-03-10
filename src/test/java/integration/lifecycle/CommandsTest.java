package integration.lifecycle;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.CommandClient;
import gr.ntua.vision.monitoring.CommandServer;
import gr.ntua.vision.monitoring.Config;
import gr.ntua.vision.monitoring.MonitoringService;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Validating the interaction between {@link CommandClient} and {@link CommandServer}.
 */
@RunWith(JMock.class)
public class CommandsTest {
    /***/
    private CommandClient           client;
    /***/
    private final Config            cnf     = new Config();
    /***/
    private final Mockery           context = new Mockery();
    /***/
    private final MonitoringService mon     = context.mock( MonitoringService.class );
    /***/
    private CommandServer           server;
    /***/
    private Thread                  t;


    /**
     * @throws SocketException
     */
    @Before
    public void setUp() throws SocketException {
        client = new CommandClient( cnf );
        server = new CommandServer( cnf, null );// FIXME
        t = new Thread( server, "command-server" );
        t.setDaemon( true );
        t.start();
    }


    /**
     * @throws InterruptedException
     */
    @After
    public void tearDown() throws InterruptedException {
        server.closeConnection();
        t.join();
    }


    /**
     * @throws IOException
     * @throws InterruptedException
     */
    @Test(expected = SocketTimeoutException.class)
    public void whenServerStoppedStatusShouldThrowTimeoutExcpetion() throws IOException, InterruptedException {
        client.stop();
        client.status();
    }


    /**
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void whenStartedStatusShouldReturnStartedVMPID() throws IOException, InterruptedException {
        assertEquals( cnf.getPID(), client.status() );
    }
}
