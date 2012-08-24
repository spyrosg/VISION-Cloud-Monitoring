package integration;

import gr.ntua.vision.monitoring.VismoFactory;
import gr.ntua.vision.monitoring.udp.UDPClient;
import gr.ntua.vision.monitoring.udp.UDPListener;
import gr.ntua.vision.monitoring.udp.UDPServer;

import java.net.DatagramSocket;
import java.net.SocketException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
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

        client.getVismoStatus();
        client.shutdownVismo();
    }


    /**
     * @throws SocketException
     */
    @Before
    public void setUp() throws SocketException {
        listener = context.mock(UDPListener.class);
        client = new UDPClient(new DatagramSocket(), PORT);
        server = new UDPServer(VismoFactory.getUDPServeSocket(PORT), listener);
        server.setDaemon(true);
        server.start();
    }


    /***/
    @After
    public void stopServer() {
        server.interrupt();
    }
}
