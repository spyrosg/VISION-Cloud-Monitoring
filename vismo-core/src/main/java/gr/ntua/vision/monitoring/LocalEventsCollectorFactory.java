package gr.ntua.vision.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 *
 */
public class LocalEventsCollectorFactory {
    /***/
    private static final Logger      log = LoggerFactory.getLogger(LocalEventsCollectorFactory.class);
    /** the configuration object. */
    private final VismoConfiguration config;
    /** the zmq context. */
    private final ZContext           ctx;


    /**
     * Constructor.
     * 
     * @param config
     *            the configuration object.
     * @param ctx
     *            the zmq context.
     */
    public LocalEventsCollectorFactory(final VismoConfiguration config, final ZContext ctx) {
        this.config = config;
        this.ctx = ctx;
    }


    /**
     * @return a setup {@link LocalEventsCollector}.
     */
    public LocalEventsCollector build() {
        return new LocalEventsCollector(getBoundPullSocket(ctx, config.getProducersPoint()),
                getConnectedPushSocket(ctx, config.getProducersPoint()));
    }


    /**
     * @param ctx
     * @param addr
     * @return a bound to the address pull socket.
     */
    private static Socket getBoundPullSocket(final ZContext ctx, final String addr) {
        final Socket sock = ctx.createSocket(ZMQ.PULL);

        sock.bind(addr);
        sock.setLinger(0);
        log.debug("new socket for listening on endpoint={}", addr);

        return sock;
    }


    /**
     * @param ctx
     * @param addr
     * @return a connected to the address push socket.
     */
    private static Socket getConnectedPushSocket(final ZContext ctx, final String addr) {
        final Socket sock = ctx.createSocket(ZMQ.PUSH);

        sock.connect(addr);
        sock.setLinger(0);
        log.debug("new socket for connecting to endpoint={}", addr);

        return sock;
    }
}
