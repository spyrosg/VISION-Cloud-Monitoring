package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.rules.AccountingRule;
import gr.ntua.vision.monitoring.rules.AggregationRule;
import gr.ntua.vision.monitoring.rules.CTORule;
import gr.ntua.vision.monitoring.scheduling.JVMStatusReportTask;
import gr.ntua.vision.monitoring.sinks.BasicEventSink;
import gr.ntua.vision.monitoring.sinks.PubSubEventSink;
import gr.ntua.vision.monitoring.sources.BasicEventSource;
import gr.ntua.vision.monitoring.udp.UDPFactory;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.net.SocketException;
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
    private final VismoVMInfo        vminfo        = new VismoVMInfo();
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
     * @return the vismo service.
     * @throws SocketException
     */
    public VismoService build() throws SocketException {
        final VismoService service = new VismoService(vminfo);

        logConfig();

        if (hostIsCloudHead())
            setupWithCloudHead(service);
        else if (hostIsClusterHead())
            setupWithClusterHead(service);
        else
            setupWithWorker(service);

        service.addTask(new UDPFactory(conf.getUDPPort()).buildServer(service));
        service.addTask(new JVMStatusReportTask(ONE_MINUTE));

        return service;
    }


    /**
     * @param zmq
     * @return
     */
    private BasicEventSource getLocalSource(final ZMQSockets zmq) {
        return new BasicEventSource(zmq.newBoundPullSocket(conf.getProducersPoint()), zmq.newConnectedPushSocket(conf
                .getProducersPoint()));
    }


    /**
     * Check whether localhost is the cluster head (according to the configuration).
     * 
     * @return <code>true</code> when localhost is a cloud head, <code>false</code> otherwise.
     * @throws SocketException
     */
    private boolean hostIsCloudHead() throws SocketException {
        return conf.isIPCloudHead(vminfo.getAddress().getHostAddress());
    }


    /**
     * Check whether localhost is a cluster head (according to the configuration).
     * 
     * @return <code>true</code> when localhost is a cluster head, <code>false</code> otherwise.
     * @throws SocketException
     */
    private boolean hostIsClusterHead() throws SocketException {
        return conf.isIPClusterHead(vminfo.getAddress().getHostAddress());
    }


    /**
     * @throws SocketException
     */
    private void logConfig() throws SocketException {
        log.debug("is cluster head? {}", hostIsClusterHead());
        log.debug("is cloud head? {}", hostIsCloudHead());
    }


    /**
     * @param service
     */
    private void setupWithCloudHead(final VismoService service) {
        // TODO Auto-generated method stub

    }


    /**
     * @param service
     */
    private void setupWithClusterHead(final VismoService service) {
        final BasicEventSource localSource = getLocalSource(zmq);
        final BasicEventSource workersSource = new BasicEventSource(
                zmq.newBoundPullSocket("tcp://*:" + conf.getClusterHeadPort()), zmq.newConnectedPushSocket("tcp://*:"
                        + conf.getClusterHeadPort()));

        service.addTask(localSource);
        service.addTask(workersSource);

        final PubSubEventSink sink = new PubSubEventSink(zmq.newBoundPubSocket("tcp://*:" + conf.getConsumersPort()));

        final RuleList everyThreeSeconds = ruleListForPeriodOf(THREE_SECONDS, new CTORule("cto-3-sec", THREE_SECONDS));
        final RuleList everyMinute = ruleListForPeriodOf(ONE_MINUTE, new CTORule("cto-1-min", ONE_MINUTE), new AccountingRule(
                ONE_MINUTE));

        final VismoAggregationTimerTask threeSecTimer = new VismoAggregationTimerTask(everyThreeSeconds, sink);
        final VismoAggregationTimerTask oneMinTimer = new VismoAggregationTimerTask(everyMinute, sink);

        service.addTask(threeSecTimer);
        service.addTask(oneMinTimer);

        localSource.subscribe(threeSecTimer);
        localSource.subscribe(oneMinTimer);
        localSource.subscribe(new PassThroughChannel(sink));
        localSource.subscribe(new SLAPerRequestChannel(sink));

        workersSource.subscribe(threeSecTimer);
        workersSource.subscribe(oneMinTimer);
        workersSource.subscribe(new PassThroughChannel(sink));
        workersSource.subscribe(new SLAPerRequestChannel(sink));
    }


    /**
     * @param service
     */
    private void setupWithWorker(final VismoService service) {
        final BasicEventSource localSource = getLocalSource(zmq);
        final BasicEventSink clusterHead = new BasicEventSink(zmq.newConnectedPushSocket("tcp://" + conf.getClusterHead() + ":"
                + conf.getClusterHeadPort()));

        localSource.subscribe(new PassThroughChannel(clusterHead));
        localSource.subscribe(new SLAPerRequestChannel(clusterHead));
        service.addTask(localSource);
    }


    /**
     * @param period
     * @param rules
     * @return
     */
    private static RuleList ruleListForPeriodOf(final long period, final AggregationRule... rules) {
        final RuleList list = new RuleList(period);

        for (final AggregationRule rule : rules)
            list.add(rule);

        return list;
    }
}
