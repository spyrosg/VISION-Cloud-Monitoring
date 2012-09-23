package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.rules.AggregationRule;
import gr.ntua.vision.monitoring.rules.CTORule;
import gr.ntua.vision.monitoring.sinks.BasicEventSink;
import gr.ntua.vision.monitoring.sources.BasicEventSource;
import gr.ntua.vision.monitoring.sources.EventSource;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class VismoClusterHead extends AbstractVismoCloudElement {
    /***/
    private static final String                        DICT_KEY      = "!dict";
    /***/
    private static final Logger                        log           = LoggerFactory.getLogger(VismoClusterHead.class);
    /***/
    private static final long                          ONE_MINUTE    = TimeUnit.MINUTES.toMillis(1);
    /***/
    private static final long                          THREE_SECONDS = TimeUnit.SECONDS.toMillis(3);
    /***/
    private final ArrayList<VismoAggregationTimerTask> ruleTasks     = new ArrayList<VismoAggregationTimerTask>();


    /**
     * Constructor.
     * 
     * @param service
     */
    public VismoClusterHead(final VismoService service) {
        super(service);
    }


    /**
     * @see gr.ntua.vision.monitoring.EventListener#receive(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void receive(final Event e) {
        @SuppressWarnings("rawtypes")
        final Map map = (Map) e.get(DICT_KEY);

        log.debug("received event from {}: {}", map.get("originating-machine"), map);

        for (final VismoAggregationTimerTask ruleTask : ruleTasks)
            ruleTask.pass(e);
    }


    /**
     * @see gr.ntua.vision.monitoring.VismoCloudElement#setup(gr.ntua.vision.monitoring.VismoConfiguration,
     *      gr.ntua.vision.monitoring.zmq.ZMQSockets)
     */
    @Override
    public void setup(final VismoConfiguration conf, final ZMQSockets zmq) {
        final BasicEventSource local = new BasicEventSource(new VismoEventFactory(), zmq.newBoundPullSocket("tcp://127.0.0.1:"
                + conf.getProducersPort()));

        attach(local);

        final BasicEventSource workers = new BasicEventSource(new VismoEventFactory(), zmq.newBoundPullSocket("tcp://*:"
                + conf.getClusterHeadPort()));

        attach(workers);

        final BasicEventSink sink = new BasicEventSink(zmq.newBoundPubSocket("tcp://*:" + conf.getConsumersPort()));

        attach(sink);

        final RuleList everyThreeSeconds = ruleListForPeriodOf(THREE_SECONDS, new CTORule("cto-3-sec", THREE_SECONDS));
        final RuleList everyMinute = ruleListForPeriodOf(ONE_MINUTE, new CTORule("cto-1-min", ONE_MINUTE));

        final VismoAggregationTimerTask three = new VismoAggregationTimerTask(everyThreeSeconds, sink);
        final VismoAggregationTimerTask one = new VismoAggregationTimerTask(everyMinute, sink);

        ruleTasks.add(three);
        ruleTasks.add(one);
        addTask(three);
        addTask(one);
    }


    /**
     * @see gr.ntua.vision.monitoring.AbstractVismoCloudElement#log()
     */
    @Override
    protected Logger log() {
        return log;
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


    /**
     * @see gr.ntua.vision.monitoring.VismoCloudElement#start()
     */
    @Override
    public void start() {
        for (final EventSource source : sources)
            service.addTask((BasicEventSource) source);
    }
}
