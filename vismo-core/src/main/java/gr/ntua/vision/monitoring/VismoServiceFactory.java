package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.rules.AccountingRule;
import gr.ntua.vision.monitoring.rules.CTORule;
import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.EventSinks;
import gr.ntua.vision.monitoring.sinks.EventSinksFactory;
import gr.ntua.vision.monitoring.sources.EventSources;
import gr.ntua.vision.monitoring.sources.EventSourcesFactory;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;


/**
 * 
 */
public class VismoServiceFactory {
    /***/
    private static final Logger      log = LoggerFactory.getLogger(VismoServiceFactory.class);
    /***/
    private final VismoConfiguration conf;


    /**
     * Constructor.
     * 
     * @param conf
     */
    public VismoServiceFactory(final VismoConfiguration conf) {
        this.conf = conf;
    }


    /**
     * @param vminfo
     * @return the {@link VismoService}.
     * @throws SocketException
     */
    public VismoService build(final VismoVMInfo vminfo) throws SocketException {
        logConfig(vminfo);

        if (hostIsCloudHead(vminfo))
            return buildCloudHead(vminfo);
        else if (hostIsClusterHead(vminfo))
            return buildClusterHead(vminfo);
        else
            return buildWorker(vminfo);
    }


    /**
     * @param vminfo
     * @return
     */
    private VismoService buildCloudHead(final VismoVMInfo vminfo) {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * @param vminfo
     * @return
     */
    private VismoClusterHeadNode buildClusterHead(final VMInfo vminfo) {
        final ZMQSockets zmq = getZMQSockets();
        final EventSourcesFactory sourcesFactory = newEventsSourceFactory(zmq);
        final EventSinksFactory sinksFactory = newEventsSinksFactory(zmq);

        final EventSources sources = sourcesFactory.createforClusterHead();
        final EventSinks sinks = sinksFactory.createForClusterHead();

        final VismoRulesEngine engine = new VismoRulesEngine(sinks);

        sources.subscribeAll(engine);
        registerDefaultRules(engine);
        registerClusterRules(engine);

        return new VismoClusterHeadNode(vminfo, sources, engine);
    }


    /**
     * @param vminfo
     * @return
     */
    private VismoWorkerNode buildWorker(final VMInfo vminfo) {
        final ZMQSockets zmq = getZMQSockets();
        final EventSourcesFactory sourcesFactory = newEventsSourceFactory(zmq);
        final EventSinksFactory sinksFactory = newEventsSinksFactory(zmq);

        final EventSources sources = sourcesFactory.createforWorker();
        final EventSinks sinks = sinksFactory.createForWorker();

        final VismoRulesEngine engine = new VismoRulesEngine(sinks);

        sources.subscribeAll(engine);
        registerDefaultRules(engine);

        return new VismoWorkerNode(vminfo, sources, engine);
    }


    /**
     * Check whether localhost is the cluster head (according to the configuration).
     * 
     * @param vminfo
     * @return <code>true</code> when localhost is a cloud head, <code>false</code> otherwise.
     * @throws SocketException
     */
    private boolean hostIsCloudHead(final VMInfo vminfo) throws SocketException {
        return conf.isIPCloudHead(vminfo.getAddress().getHostAddress());
    }


    /**
     * Check whether localhost is a cluster head (according to the configuration).
     * 
     * @param vminfo
     * @return <code>true</code> when localhost is a cluster head, <code>false</code> otherwise.
     * @throws SocketException
     */
    private boolean hostIsClusterHead(final VMInfo vminfo) throws SocketException {
        return conf.isIPClusterHead(vminfo.getAddress().getHostAddress());
    }


    /**
     * @param vminfo
     * @throws SocketException
     */
    private void logConfig(final VMInfo vminfo) throws SocketException {
        log.debug("is cluster head? {}", hostIsClusterHead(vminfo));
        log.debug("is cloud head? {}", hostIsCloudHead(vminfo));
    }


    /**
     * @param zmq
     * @return
     */
    private EventSinksFactory newEventsSinksFactory(final ZMQSockets zmq) {
        return new EventSinksFactory(conf, zmq);
    }


    /**
     * @param zmq
     * @return
     */
    private EventSourcesFactory newEventsSourceFactory(final ZMQSockets zmq) {
        return new EventSourcesFactory(conf, zmq);
    }


    /**
     * @return
     */
    private static ZMQSockets getZMQSockets() {
        return new ZMQSockets(new ZContext());
    }


    /**
     * @param engine
     */
    private static void registerClusterRules(final VismoRulesEngine engine) {
        // TODO: rename method

        final long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);
        final long THREE_SECONDS = TimeUnit.SECONDS.toMillis(3);

        new CTORule(engine, "cto-3-sec", THREE_SECONDS).submitTo(engine);
        new CTORule(engine, "cto-1-min", ONE_MINUTE).submitTo(engine);
        new AccountingRule(engine, ONE_MINUTE).submitTo(engine);
    }


    /**
     * @param engine
     */
    private static void registerDefaultRules(final VismoRulesEngine engine) {
        new PassThroughRule(engine).submitTo(engine);
        // TODO: add SLAPerRequest rule
    }
}
