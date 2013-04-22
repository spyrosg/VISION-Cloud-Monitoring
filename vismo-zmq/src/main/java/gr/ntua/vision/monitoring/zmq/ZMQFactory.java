package gr.ntua.vision.monitoring.zmq;

import gr.ntua.vision.monitoring.sockets.Socket;
import gr.ntua.vision.monitoring.sockets.SocketFactory;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;


/**
 * A helper object over zmq contexts and sockets. This is used mainly to encapsulate and guarantee the use of just one
 * {@link ZContext} in the entire application.
 */
public class ZMQFactory implements SocketFactory {
    /** the context. */
    private final ZContext ctx;


    /**
     * Constructor.
     * 
     * @param ctx
     *            the context.
     */
    public ZMQFactory(final ZContext ctx) {
        this.ctx = ctx;
    }


    /**
     * Release the zmq context.
     */
    public void destroy() {
        ctx.destroy();
    }


    /**
     * @see gr.ntua.vision.monitoring.sockets.SocketFactory#newBoundPullSocket(java.lang.String)
     */
    @Override
    public Socket newBoundPullSocket(final String addr) {
        return new ZMQSocket(createBoundSocket(ZMQ.PULL, addr), addr);
    }


    /**
     * @see gr.ntua.vision.monitoring.sockets.SocketFactory#newBoundPushSocket(java.lang.String)
     */
    @Override
    public Socket newBoundPushSocket(final String addr) {
        return new ZMQSocket(createBoundSocket(ZMQ.PUSH, addr), addr);
    }


    /**
     * @see gr.ntua.vision.monitoring.sockets.SocketFactory#newConnectedPullSocket(java.lang.String)
     */
    @Override
    public Socket newConnectedPullSocket(final String addr) {
        return new ZMQSocket(createConnectedSocket(ZMQ.PULL, addr), addr);
    }


    /**
     * @see gr.ntua.vision.monitoring.sockets.SocketFactory#newConnectedPushSocket(java.lang.String)
     */
    @Override
    public Socket newConnectedPushSocket(final String addr) {
        return new ZMQSocket(createConnectedSocket(ZMQ.PUSH, addr), addr);
    }


    /**
     * @see gr.ntua.vision.monitoring.sockets.SocketFactory#newPubSocket(java.lang.String)
     */
    @Override
    public Socket newPubConnectSocket(final String addr) {
        return new ZMQSocket(createConnectedSocket(ZMQ.PUB, addr), addr);
    }


    /**
     * @see gr.ntua.vision.monitoring.sockets.SocketFactory#newPubSocket(java.lang.String)
     */
    @Override
    public Socket newPubSocket(final String addr) {
        return new ZMQSocket(createBoundSocket(ZMQ.PUB, addr), addr);
    }


    /**
     * @see gr.ntua.vision.monitoring.sockets.SocketFactory#newSubSocket(java.lang.String, java.lang.String)
     */
    @Override
    public Socket newSubSocket(final String addr, final String topic) {
        final ZMQ.Socket sock = createConnectedSocket(ZMQ.SUB, addr);

        sock.subscribe(topic.getBytes());

        return new ZMQSocket(sock, addr);
    }


    /**
     * Return a zmq socket, connected to given address. The socket's send/receive return immediately and won't linger.
     * 
     * @param type
     *            the type of the socket.
     * @param addr
     *            the address to bind.
     * @return a zmq socket.
     */
    private ZMQ.Socket createBoundSocket(final int type, final String addr) {
        final ZMQ.Socket sock = ctx.createSocket(type);

        sock.setLinger(0);
        sock.setSendTimeOut(0);

        try {
            sock.bind(addr);
        } catch (final ZMQException e) {
            throw new RuntimeException("cannot bind to '" + addr + "'", e);
        }

        return sock;
    }


    /**
     * Return a zmq socket, bound to given address. The socket's send/receive return immediately and won't linger.
     * 
     * @param type
     *            the type of the socket.
     * @param addr
     *            the address to bind.
     * @return a zmq socket.
     */
    private ZMQ.Socket createConnectedSocket(final int type, final String addr) {
        final ZMQ.Socket sock = ctx.createSocket(type);

        sock.setLinger(0);
        // sock.setSendTimeOut(0);

        try {
            sock.connect(addr);
        } catch (final ZMQException e) {
            throw new RuntimeException("cannot connect to '" + addr + "'", e);
        }

        return sock;
    }
}
