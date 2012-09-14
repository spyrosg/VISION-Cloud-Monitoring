package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.AggregationRule;
import gr.ntua.vision.monitoring.rules.CTORule;
import gr.ntua.vision.monitoring.scheduling.JVMStatusReportTask;
import gr.ntua.vision.monitoring.udp.UDPFactory;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import org.zeromq.ZContext;


/**
 * This is used to configure and properly initialize the application graph.
 */
public class VismoNodeFactory {
    /***/
    private static final long        ONE_MINUTE    = TimeUnit.MINUTES.toMillis(1);
    /***/
    private static final String[]    operations    = { "GET", "PUT", "DELETE" };
    /***/
    private static final long        THREE_SECONDS = TimeUnit.SECONDS.toMillis(3);
    /** the configuration object. */
    private final VismoConfiguration conf;


    /**
     * Constructor.
     * 
     * @param conf
     *            the configuration object.
     */
    public VismoNodeFactory(final VismoConfiguration conf) {
        this.conf = conf;
    }


    /**
     * Configure and return a {@link OldVismoNode} instance.
     * 
     * @param listeners
     * @return a new vismo instance.
     * @throws SocketException
     */
    public OldVismoNode build(final EventListener... listeners) throws SocketException {
        final ZMQSockets zmq = new ZMQSockets(new ZContext());
        final OldVismoNode vismo = new OldVismoNode(new VismoVMInfo());

        final VismoEventSource receiver = new VismoEventSourceFactory(conf).build(zmq);
        final EventDistributor distributor = new EventDistributor(zmq.newBoundPubSocket(conf.getConsumersPoint()));

        for (final EventListener listener : listeners)
            receiver.subscribe(listener);

        addPassThroughListener(receiver, distributor);

        // TODO: move rules registration and timers to a file

        final RuleList everyThreeSeconds = ruleListForPeriodOf(THREE_SECONDS, new CTORule("cto-3-sec", THREE_SECONDS));
        final RuleList everyMinute = ruleListForPeriodOf(ONE_MINUTE, new CTORule("cto-1-min", ONE_MINUTE));

        registerRuleTimerTask(vismo, receiver, getTimerFor(distributor, everyThreeSeconds));
        registerRuleTimerTask(vismo, receiver, getTimerFor(distributor, everyMinute));

        vismo.addTimerTask(new JVMStatusReportTask(ONE_MINUTE));
        vismo.addTask(new UDPFactory(conf.getUDPPort()).buildServer(vismo));
        vismo.addTask(receiver);

        return vismo;
    }


    /**
     * A pass through listener is just used to pass events from the receiver directly to the distributor.
     * 
     * @param receiver
     * @param distributor
     */
    private static void addPassThroughListener(final EventSource receiver, final EventDistributor distributor) {
        receiver.subscribe(new EventListener() {
            @Override
            public void receive(final Event e) {
                distributor.serialize(e);
            }


            @Override
            public String toString() {
                return "#<PassThroughEventListener>";
            }
        });
    }


    /**
     * @param distributor
     * @param list
     * @return
     */
    private static VismoAggregationTimerTask getTimerFor(final EventDistributor distributor, final RuleList list) {
        return new VismoAggregationTimerTask(distributor, list);
    }


    /**
     * @param vismo
     * @param receiver
     * @param ruleTimer
     */
    private static void registerRuleTimerTask(final OldVismoNode vismo, final EventSource receiver,
            final VismoAggregationTimerTask ruleTimer) {
        receiver.subscribe(ruleTimer);
        vismo.addTimerTask(ruleTimer);
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
