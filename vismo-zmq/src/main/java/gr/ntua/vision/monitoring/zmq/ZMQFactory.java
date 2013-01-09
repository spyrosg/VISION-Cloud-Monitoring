package gr.ntua.vision.monitoring.zmq;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQException;


/**
 * A helper object over zmq contexts and sockets. This is used mainly to encapsulate and guarantee the use of just one
 * {@link ZContext} in the entire application.
 */
public class ZMQFactory {
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
     * @param addr
     *            the address to bind to.
     * @return a bound to the address pull socket.
     */
    public VismoSocket newBoundPullSocket(final String addr) {
        return new VismoSocket(createBoundSocket(ZMQ.PULL, addr), addr);
    }


    /**
     * @param addr
     *            the address to bind to.
     * @return a bound to the address pull socket.
     */
    public VismoSocket newBoundPushSocket(final String addr) {
        return new VismoSocket(createBoundSocket(ZMQ.PUSH, addr), addr);
    }


    /**
     * @param addr
     *            the address to connect to.
     * @return a connected to the address push socket.
     */
    public VismoSocket newConnectedPullSocket(final String addr) {
        return new VismoSocket(createConnectedSocket(ZMQ.PULL, addr), addr);
    }


    /**
     * @param addr
     *            the address to connect to.
     * @return a connected to the address push socket.
     */
    public VismoSocket newConnectedPushSocket(final String addr) {
        return new VismoSocket(createConnectedSocket(ZMQ.PUSH, addr), addr);
    }


    /**
     * @param addr
     *            the address to bind to.
     * @return a new bound publish socket.
     */
    public VismoSocket newPubSocket(final String addr) {
        return new VismoSocket(createBoundSocket(ZMQ.PUB, addr), addr);
    }


    /**
     * @param addr
     *            the address to connect to.
     * @param topic
     *            the topic to subscribe to.
     * @return a connected socket, subscribed to the given topic.
     */
    public VismoSocket newSubSocket(final String addr, final String topic) {
        final Socket sock = createConnectedSocket(ZMQ.SUB, addr);

        sock.subscribe(topic.getBytes());

        return new VismoSocket(sock, addr);
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
    private Socket createBoundSocket(final int type, final String addr) {
        final Socket sock = ctx.createSocket(type);

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
    private Socket createConnectedSocket(final int type, final String addr) {
        final Socket sock = ctx.createSocket(type);

        sock.setLinger(0);
        sock.setSendTimeOut(0);

        try {
            sock.connect(addr);
        } catch (final ZMQException e) {
            throw new RuntimeException("cannot connect to '" + addr + "'", e);
        }

        return sock;
    }
}
