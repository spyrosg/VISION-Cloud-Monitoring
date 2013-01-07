package gr.ntua.vision.monitoring.zmq;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 * A helper object over zmq contexts and sockets. This is used mainly to encapsulate and guarantee the use of just one
 * {@link ZContext} in the entire application.
 */
public class ZMQSockets {
    /** the context. */
    private final ZContext ctx;


    /**
     * Constructor.
     * 
     * @param ctx
     *            the context.
     */
    public ZMQSockets(final ZContext ctx) {
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
     * @return a new bound pub socket.
     */
    public VismoSocket newBoundPubSocket(final String addr) {
        final Socket sock = ctx.createSocket(ZMQ.PUB);

        sock.setLinger(0);
        sock.setSendTimeOut(0);
        sock.bind(addr);

        return new VismoSocket(sock, addr);
    }


    /**
     * @param addr
     *            the address to bind to.
     * @return a bound to the address pull socket.
     */
    public VismoSocket newBoundPullSocket(final String addr) {
        final Socket sock = ctx.createSocket(ZMQ.PULL);

        sock.setLinger(0);
        sock.bind(addr);

        return new VismoSocket(sock, addr);
    }


    /**
     * @param addr
     *            the address to bind to.
     * @return a bound to the address pull socket.
     */
    public VismoSocket newBoundPushSocket(final String addr) {
        final Socket sock = ctx.createSocket(ZMQ.PUSH);

        sock.setLinger(0);
        sock.bind(addr);

        return new VismoSocket(sock, addr);
    }


    /**
     * @param addr
     *            the address to connect to.
     * @return a connected to the address push socket.
     */
    public VismoSocket newConnectedPullSocket(final String addr) {
        final Socket sock = ctx.createSocket(ZMQ.PULL);

        sock.setLinger(0);
        sock.connect(addr);

        return new VismoSocket(sock, addr);
    }


    /**
     * @param addr
     *            the address to connect to.
     * @return a connected to the address push socket.
     */
    public VismoSocket newConnectedPushSocket(final String addr) {
        final Socket sock = ctx.createSocket(ZMQ.PUSH);

        sock.setLinger(0);
        sock.connect(addr);

        return new VismoSocket(sock, addr);
    }


    /**
     * @param addr
     *            the address to connect to.
     * @param topic
     *            the topic to subscribe to.
     * @return a connected socket, subscribed to the given topic.
     */
    public VismoSocket newSubSocketForTopic(final String addr, final String topic) {
        final Socket sock = ctx.createSocket(ZMQ.SUB);

        sock.setLinger(0);
        sock.connect(addr);
        sock.subscribe(topic.getBytes());

        return new VismoSocket(sock, addr);
    }
}
