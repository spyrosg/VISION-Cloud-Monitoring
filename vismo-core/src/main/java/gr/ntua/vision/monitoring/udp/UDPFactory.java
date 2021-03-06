package gr.ntua.vision.monitoring.udp;

import java.net.DatagramSocket;
import java.net.SocketException;


/**
 * A helper object for localizing the creation of upd servers and clients. Each factory will be able to create "symmetric" clients
 * and servers, in the sense that a client will only talk to the server listening to the given port.
 */
public class UDPFactory {
    /** the port number to use. */
    private final int port;


    /**
     * Constructor.
     * 
     * @param port
     *            the port number to use.
     */
    public UDPFactory(final int port) {
        this.port = port;
    }


    /**
     * @return a udp client.
     * @throws SocketException
     */
    public UDPClient buildClient() throws SocketException {
        return new UDPClient(port);
    }


    /**
     * @return a udp server.
     * @throws SocketException
     */
    public UDPServer buildServer() throws SocketException {
        return new UDPServer(new DatagramSocket(port));
    }
}
