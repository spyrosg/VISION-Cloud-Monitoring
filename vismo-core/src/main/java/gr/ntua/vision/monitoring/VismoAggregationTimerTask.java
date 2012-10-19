package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.AggregationRule;
import gr.ntua.vision.monitoring.rules.VismoRuleAggregationListener;
import gr.ntua.vision.monitoring.scheduling.VismoRepeatedTask;
import gr.ntua.vision.monitoring.sinks.EventSink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is responsible for executing the rules at the end of each period.
 */
public class VismoAggregationTimerTask extends VismoRepeatedTask implements EventSourceListener {
    /***/
    private static final Logger log   = LoggerFactory.getLogger(VismoAggregationTimerTask.class);
    /** the aggregation period. */
    private final long          period;
    /***/
    private final RuleList      rules = new RuleList();
    /***/
    private final EventSink     sink;


    /**
     * Constructor.
     * 
     * @param period
     * @param sink
     */
    public VismoAggregationTimerTask(final long period, final EventSink sink) {
        this.period = period;
        this.sink = sink;
    }


    /**
     * @return the period.
     */
    @Override
    public long getPeriod() {
        return period;
    }


    /**
     * @param e
     */
    @Override
    public void receive(final Event e) {
        rules.matchToEvent(e);
    }


    /**
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        log.trace("starting aggregation");

        final long start = System.currentTimeMillis();
        final VismoRuleAggregationListener aggregationListener = new VismoRuleAggregationListener(sink, period, start);

        rules.runRules(aggregationListener);

        final long end = System.currentTimeMillis();

        log.trace("ending aggregation in {} seconds", (end - start) / 1000.0);
    }


    /**
     * @param rule
     */
    public void submit(final AggregationRule rule) {
        rules.add(rule);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<VismoAggregationTimerTask: running every " + (getPeriod() / 1000) + " second(s)>";
    }
}
