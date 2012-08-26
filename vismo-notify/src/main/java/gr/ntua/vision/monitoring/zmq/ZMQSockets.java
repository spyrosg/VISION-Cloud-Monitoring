package gr.ntua.vision.monitoring.zmq;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 * A helper object over zmq contexts and sockets. This is used mainly to encapsulate and guarantee the use of just one
 * {@link ZContext} in the entire application. TODO: move to vismo-events.
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
     * @param addr
     *            the address to bind to.
     * @return a new bound pub socket.
     */
    public Socket newBoundPubSocket(final String addr) {
        final Socket sock = ctx.createSocket(ZMQ.PUB);

        sock.setLinger(0);
        sock.setSendTimeOut(0); // FIXME: non-blocking for now
        sock.bind(addr);

        return sock;
    }


    /**
     * @param addr
     *            the address to bind to.
     * @return a bound to the address pull socket.
     */
    public Socket newBoundPullSocket(final String addr) {
        final Socket sock = ctx.createSocket(ZMQ.PULL);

        sock.bind(addr);
        sock.setLinger(0);

        return sock;
    }


    /**
     * @param addr
     *            the address to connect to.
     * @return a connected to the address push socket.
     */
    public Socket newConnectedPushSocket(final String addr) {
        final Socket sock = ctx.createSocket(ZMQ.PUSH);

        sock.connect(addr);
        sock.setLinger(0);

        return sock;
    }


    /**
     * @param addr
     * @param topic
     * @return
     */
    public Socket newPubSocketForTopic(final String addr, final String topic) {
        final Socket sock = ctx.createSocket(ZMQ.SUB);

        sock.setLinger(0);
        sock.connect(addr);
        sock.subscribe(topic.getBytes());

        return sock;
    }
}
