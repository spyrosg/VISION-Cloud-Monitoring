package gr.ntua.vision.monitoring.udp;

import java.net.DatagramSocket;
import java.net.SocketException;


/**
 * TODO: make UDPClient/Server constructors package protected.
 */
public class UDPFactory {
    /***/
    private final int port;


    /**
     * @param port
     */
    public UDPFactory(final int port) {
        this.port = port;
    }


    /**
     * @return a udp client.
     * @throws SocketException
     */
    public UDPClient buildClient() throws SocketException {
        return new UDPClient(new DatagramSocket(), port);
    }


    /**
     * @param listener
     * @return a upd server.
     * @throws SocketException
     */
    public UDPServer buildServer(final UDPListener listener) throws SocketException {
        return new UDPServer(new DatagramSocket(port), listener);
    }
}
