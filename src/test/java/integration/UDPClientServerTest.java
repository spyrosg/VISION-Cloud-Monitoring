package integration;

import gr.ntua.vision.monitoring.UDPClient;
import gr.ntua.vision.monitoring.UDPListener;
import gr.ntua.vision.monitoring.UDPServer;

import java.net.SocketException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class UDPClientServerTest {
    /***/
    private static final int PORT    = 57890;
    /***/
    UDPListener              listener;
    /***/
    private UDPClient        client;
    /***/
    private final Mockery    context = new JUnit4Mockery();
    /***/
    private UDPServer        server;


    /**
     * @throws Exception
     */
    @Test
    public void serverNotifiesListenerOnClientRequests() throws Exception {
        context.checking(new Expectations() {
            {
                exactly(2).of(listener).notify(with(any(String.class)));
            }
        });

        client.getServiceStatus();
        client.shutdownService();
        stopServer();
    }


    /**
     * @throws SocketException
     */
    @Before
    public void setUp() throws SocketException {
        listener = context.mock(UDPListener.class);
        client = new UDPClient(PORT);
        server = new UDPServer(PORT, listener);
        server.setDaemon(true);
        server.start();
    }


    /***/
    public void stopServer() {
        server.shutDown();
    }
}
