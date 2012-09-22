package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.AggregationRule;
import gr.ntua.vision.monitoring.rules.CTORule;
import gr.ntua.vision.monitoring.sources.EventSource;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class VismoClusterHead extends AbstractVismoCloudElement {
    /***/
    private static final String DICT_KEY      = "!dict";
    /***/
    private static final Logger log           = LoggerFactory.getLogger(VismoClusterHead.class);
    /***/
    private static final long   ONE_MINUTE    = TimeUnit.MINUTES.toMillis(1);
    /***/
    private static final long   THREE_SECONDS = TimeUnit.SECONDS.toMillis(3);


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

        log.trace("received event from {}: {}", map.get("originating-machine"), map);
        aggregate();
        send(e);
    }


    /**
     * @see gr.ntua.vision.monitoring.VismoCloudElement#setup(gr.ntua.vision.monitoring.VismoConfiguration,
     *      gr.ntua.vision.monitoring.zmq.ZMQSockets)
     */
    @Override
    public void setup(final VismoConfiguration conf, final ZMQSockets zmq) {
        // TODO Auto-generated method stub

    }


    /**
     * @see gr.ntua.vision.monitoring.AbstractVismoCloudElement#start()
     */
    @Override
    public void start() {
        super.start();

        final RuleList everyThreeSeconds = ruleListForPeriodOf(THREE_SECONDS, new CTORule("cto-3-sec", THREE_SECONDS));
        final RuleList everyMinute = ruleListForPeriodOf(ONE_MINUTE, new CTORule("cto-1-min", ONE_MINUTE));

        for (final EventSource source : sources) {
            source.subscribe(new VismoAggregationTimerTask(everyThreeSeconds, sink));
            source.subscribe(new VismoAggregationTimerTask(everyMinute, sink));
        }
    }


    /**
     * @see gr.ntua.vision.monitoring.AbstractVismoCloudElement#log()
     */
    @Override
    protected Logger log() {
        return log;
    }


    /**
     * 
     */
    private void aggregate() {
        // TODO Auto-generated method stub

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
