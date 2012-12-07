package integration;

import gr.ntua.vision.monitoring.udp.UDPClient;
import gr.ntua.vision.monitoring.udp.UDPFactory;
import gr.ntua.vision.monitoring.udp.UDPListener;
import gr.ntua.vision.monitoring.udp.UDPServer;

import java.net.SocketException;
import java.util.List;

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
    @SuppressWarnings("unchecked")
    @Test
    public void serverNotifiesListenerOnClientRequests() throws Exception {
        context.checking(new Expectations() {
            {
                exactly(1).of(listener).halt();
                exactly(1).of(listener).collectStatus(with(any(List.class)));
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
        final UDPFactory udpFactory = new UDPFactory(PORT);
        client = udpFactory.buildClient();
        server = udpFactory.buildServer();
        server.add(listener);
        server.setDaemon(true);
        server.start();
    }


    /***/
    @After
    public void stopServer() {
        server.interrupt();
    }
}
