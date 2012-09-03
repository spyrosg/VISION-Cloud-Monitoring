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
    /***/
    private static final int CLIENT_TIMEOUT = 1000;

    // FIXME: remove udp port from config
    // FIXME: the stop/status reporting

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
    	final DatagramSocket sock = new DatagramSocket();
    	
    	sock.setSoTimeout(CLIENT_TIMEOUT);
    	
        return new UDPClient(sock, port);
    }


    /**
     * @param listener
     *            the listener.
     * @return a upd server.
     * @throws SocketException
     */
    public UDPServer buildServer(final UDPListener listener) throws SocketException {
        return new UDPServer(new DatagramSocket(port), listener);
    }
}
