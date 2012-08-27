package gr.ntua.vision.monitoring.zmq;

import gr.ntua.vision.monitoring.notify.EventRegistry;

import java.util.logging.Logger;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 * A helper object over zmq contexts and sockets. This is used mainly to encapsulate and guarantee the use of just one
 * {@link ZContext} in the entire application.
 */
public class ZMQSockets {
    /** the log target. */
    private static final Logger log = Logger.getLogger(ZMQSockets.class.getName());
    /** the context. */
    private final ZContext      ctx;

    static {
        EventRegistry.activateLogger();
    }


    // TODO: maybe use a builder?

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
        log.config("new PUB zsocket bound to port=" + addr);

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
        log.config("new PULL zsocket bound to port=" + addr);

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
        log.config("new PUSH zsocket connecting on port=" + addr);

        final Socket sock = ctx.createSocket(ZMQ.PUSH);

        sock.connect(addr);
        sock.setLinger(0);

        return sock;
    }


    /**
     * @param addr
     *            the address to connect to.
     * @param topic
     *            the topic to subscribe to.
     * @return a connected socket, subscribed to the given topic.
     */
    public Socket newSubSocketForTopic(final String addr, final String topic) {
        log.config("new SUB zsocket connecting on port=" + addr + ", for topic=" + topic);

        final Socket sock = ctx.createSocket(ZMQ.SUB);

        sock.setLinger(0);
        sock.connect(addr);
        sock.subscribe(topic.getBytes());

        return sock;
    }
}
