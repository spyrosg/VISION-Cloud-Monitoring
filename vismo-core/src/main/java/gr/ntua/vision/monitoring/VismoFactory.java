package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.AccountingRule;
import gr.ntua.vision.monitoring.rules.CTORule;
import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.RuleProc;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.scheduling.JVMStatusReportTask;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sinks.EventSinks;
import gr.ntua.vision.monitoring.sinks.UniqueEventSink;
import gr.ntua.vision.monitoring.sources.BasicEventSource;
import gr.ntua.vision.monitoring.udp.UDPFactory;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;


/**
 * 
 */
public class VismoFactory {
    /***/
    private static final Logger      log           = LoggerFactory.getLogger(VismoFactory.class);
    /***/
    private static final long        ONE_MINUTE    = TimeUnit.MINUTES.toMillis(1);
    /***/
    private static final long        THREE_SECONDS = TimeUnit.SECONDS.toMillis(3);
    /***/
    private final VismoConfiguration conf;
    /***/
    private final ZMQSockets         zmq           = new ZMQSockets(new ZContext());


    /**
     * Constructor.
     * 
     * @param conf
     */
    public VismoFactory(final VismoConfiguration conf) {
        this.conf = conf;
    }


    /**
     * @param vminfo
     * @return the vismo service.
     * @throws SocketException
     */
    public VismoService build(final VMInfo vminfo) throws SocketException {
        final VismoService service = new VismoService(vminfo);

        logConfig(vminfo);

        if (hostIsCloudHead(vminfo))
            setupWithCloudHead(service);
        else if (hostIsClusterHead(vminfo))
            setupWithClusterHead(service);
        else
            setupWithWorker(service);

        service.addTask(new UDPFactory(conf.getUDPPort()).buildServer(service));
        service.addTask(new JVMStatusReportTask(ONE_MINUTE));

        return service;
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
     * @return the localhost source.
     */
    private BasicEventSource localSource() {
        return new BasicEventSource(zmq.newBoundPullSocket(conf.getProducersPoint()), zmq.newConnectedPushSocket(conf
                .getProducersPoint()));
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
     * @param service
     */
    private void setupWithCloudHead(final VismoService service) {
        /*final BasicEventSource localSource = localSource();
        final BasicEventSource clusterHeadsSource = sourceforAddress("tcp://*:" + conf.getCloudHeadPort());

        final EventSink sink = new UniqueEventSink(zmq.newBoundPubSocket("tcp://*:" + conf.getConsumersPort()));
        final VismoAggregationTimerTask threeSecTimer = new VismoAggregationTimerTask(THREE_SECONDS, sink);
        final VismoAggregationTimerTask oneMinTimer = new VismoAggregationTimerTask(ONE_MINUTE, sink);

        for (final BasicEventSource source : new BasicEventSource[] { localSource, clusterHeadsSource }) {
            source.add(threeSecTimer);
            source.add(oneMinTimer);
            source.add(new PassThroughChannel(sink));
            source.add(new SLAPerRequestChannel(sink));

            service.addTask(source);
        }

        // service.addTask(threeSecTimer);
        // service.addTask(oneMinTimer);

        submitRules(threeSecTimer, new CTORule("cto-3-sec", THREE_SECONDS));
        submitRules(oneMinTimer, new CTORule("cto-1-min", ONE_MINUTE), new AccountingRule(ONE_MINUTE));
        */
    }


    /**
     * @param service
     */
    private void setupWithClusterHead(final VismoService service) {
        final EventSink sink = new UniqueEventSink(zmq.newBoundPubSocket("tcp://*:" + conf.getConsumersPort()));
        final EventSink cloudSink = new UniqueEventSink(zmq.newConnectedPushSocket("tcp://" + conf.getCloudHeads().get(0) + ":"
                + conf.getCloudHeadPort()));
        final VismoRulesEngine engine = new VismoRulesEngine(new EventSinks(sink, cloudSink));
        final BasicEventSource localSource = localSource();
        final BasicEventSource workersSource = sourceforAddress("tcp://*:" + conf.getClusterHeadPort());

        for (final BasicEventSource source : new BasicEventSource[] { localSource, workersSource }) {
            engine.registerWithSource(source);
            service.addTask(source);
        }

        for (final RuleProc<Event> rule : getRules(engine))
            rule.submitTo(engine);
    }


    /**
     * @param service
     */
    private void setupWithWorker(final VismoService service) {
        final BasicEventSource localSource = localSource();
        final UniqueEventSink clusterHeadSink = new UniqueEventSink(zmq.newConnectedPushSocket("tcp://" + conf.getClusterHead()
                + ":" + conf.getClusterHeadPort()));

        // FIXME: use rules
        // localSource.add(new PassThroughChannel(clusterHeadSink));
        // localSource.add(new SLAPerRequestChannel(clusterHeadSink));
        service.addTask(localSource);
    }


    /**
     * @param address
     * @return the event source for given address
     */
    private BasicEventSource sourceforAddress(final String address) {
        return new BasicEventSource(zmq.newBoundPullSocket(address), zmq.newConnectedPushSocket(address));
    }


    /**
     * @param engine
     * @return
     */
    @SuppressWarnings("unchecked")
    private static List<RuleProc<Event>> getRules(final VismoRulesEngine engine) {
        return Arrays.asList(new CTORule(engine, "cto-3-sec", THREE_SECONDS), new CTORule(engine, "cto-1-min", ONE_MINUTE),
                             new AccountingRule(engine, ONE_MINUTE), new PassThroughRule(engine));
    }
}
