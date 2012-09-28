package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.sinks.EventSink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class VismoRuleAggregationListener implements RuleAggregationListener {
    /***/
    private final long      aggregationPeriodEnd;
    /***/
    private final Logger    log = LoggerFactory.getLogger(VismoRuleAggregationListener.class);
    /***/
    private final EventSink sink;


    /**
     * Constructor.
     * 
     * @param sink
     * @param aggregationPeriodEnd
     */
    public VismoRuleAggregationListener(final EventSink sink, final long aggregationPeriodEnd) {
        this.sink = sink;
        this.aggregationPeriodEnd = aggregationPeriodEnd;
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleAggregationListener#endAggregation(gr.ntua.vision.monitoring.rules.AggregationRule,
     *      gr.ntua.vision.monitoring.rules.AggregationResult)
     */
    @Override
    public void endAggregation(final AggregationRule rule, final AggregationResult result) {
        result.puttStart(aggregationPeriodEnd - rule.aggregationPeriod());
        result.puttEnd(aggregationPeriodEnd);
        log.debug("ending {}", rule, result);
        sink.send(result);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleAggregationListener#startAggregation(gr.ntua.vision.monitoring.rules.AggregationRule)
     */
    @Override
    public void startAggregation(final AggregationRule rule) {
        log.debug("starting {}", rule);
    }
}
