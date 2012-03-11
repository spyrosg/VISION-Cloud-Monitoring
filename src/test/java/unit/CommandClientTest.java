package unit;

import gr.ntua.vision.monitoring.CommandClient;
import gr.ntua.vision.monitoring.Config;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/***/
public class CommandClientTest {
    /***/
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    /***/
    private CommandClient          client;
    /***/
    private final Config           cnf       = new Config();


    /**
     * @throws SocketException
     */
    @Before
    public void setUp() throws SocketException {
        client = new CommandClient( cnf );
    }


    /**
     * @throws IOException
     */
    @Test
    public void statusShouldTimeoutExceptionWhenThereIsNoServer() throws IOException {
        exception.expect( SocketTimeoutException.class );
        client.status();
    }


    /**
     * @throws IOException
     */
    @Test
    public void stopShouldTimeoutWhenThereIsNoServer() throws IOException {
        exception.expect( SocketTimeoutException.class );
        client.stop();
    }
}
