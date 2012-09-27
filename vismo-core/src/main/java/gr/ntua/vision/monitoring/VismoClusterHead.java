package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.rules.AggregationRule;
import gr.ntua.vision.monitoring.rules.CTORule;
import gr.ntua.vision.monitoring.sinks.BasicEventSink;
import gr.ntua.vision.monitoring.sources.BasicEventSource;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class VismoClusterHead implements VismoCloudElement {
    /***/
    private static final Logger      log           = LoggerFactory.getLogger(VismoClusterHead.class);
    /***/
    private static final long        ONE_MINUTE    = TimeUnit.MINUTES.toMillis(1);
    /***/
    private static final long        THREE_SECONDS = TimeUnit.SECONDS.toMillis(3);
    /***/
    private final VismoConfiguration conf;
    /***/
    private final VismoService       service;
    /***/
    private final ZMQSockets         zmq;


    /**
     * Constructor.
     * 
     * @param service
     * @param zmq
     * @param conf
     */
    public VismoClusterHead(final VismoService service, final VismoConfiguration conf, final ZMQSockets zmq) {
        this.service = service;
        this.conf = conf;
        this.zmq = zmq;
    }


    /**
     * @see gr.ntua.vision.monitoring.VismoCloudElement#setup()
     */
    @Override
    public void setup() {
        log.debug("setting up");

        final BasicEventSource local = new BasicEventSource(new VismoEventFactory(), zmq.newBoundPullSocket(conf
                .getProducersPoint()));

        service.addTask(local);

        final BasicEventSource workers = new BasicEventSource(new VismoEventFactory(), zmq.newBoundPullSocket("tcp://*:"
                + conf.getClusterHeadPort()));

        service.addTask(workers);

        final BasicEventSink sink = new BasicEventSink(zmq.newBoundPubSocket("tcp://*:" + conf.getConsumersPort()));

        final RuleList everyThreeSeconds = ruleListForPeriodOf(THREE_SECONDS, new CTORule("cto-3-sec", THREE_SECONDS));
        final RuleList everyMinute = ruleListForPeriodOf(ONE_MINUTE, new CTORule("cto-1-min", ONE_MINUTE));

        final VismoAggregationTimerTask three = new VismoAggregationTimerTask(everyThreeSeconds, sink);
        final VismoAggregationTimerTask one = new VismoAggregationTimerTask(everyMinute, sink);

        local.subscribe(three);
        local.subscribe(one);
        workers.subscribe(three);
        workers.subscribe(one);

        local.subscribe(new EventListener() {
            @Override
            public void receive(final Event e) {
                sink.send(e);
            }
        });

        workers.subscribe(new EventListener() {
            @Override
            public void receive(final Event e) {
                sink.send(e);
            }
        });

        service.addTask(three);
        service.addTask(one);
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
