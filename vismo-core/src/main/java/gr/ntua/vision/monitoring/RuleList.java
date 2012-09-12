package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.AggregationResultEvent;
import gr.ntua.vision.monitoring.rules.AggregationRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to represent a list of rules, all running at the end of the same period.
 */
public class RuleList {
    /***/
    private static final Logger                     log          = LoggerFactory.getLogger(RuleList.class);
    /***/
    private final Map<AggregationRule, List<Event>> eventBuckets = new HashMap<AggregationRule, List<Event>>();
    /***/
    private final ArrayList<AggregationRule>        list         = new ArrayList<AggregationRule>();
    /***/
    private final long                              period;


    /**
     * Constructor.
     * 
     * @param period
     */
    public RuleList(final long period) {
        this.period = period;
    }


    /**
     * @param rule
     */
    public void add(final AggregationRule rule) {
        log.debug("registering rule {}", rule);
        list.add(rule);
    }


    /**
     * @return the period
     */
    public long getPeriod() {
        return period;
    }


    /**
     * @param e
     */
    public void matchToEvent(final Event e) {
        for (final AggregationRule rule : list)
            if (rule.matches(e))
                appendToBucket(rule, e);
    }


    /**
     * @param aggregationPeriodTimestamp
     * @param distributor
     */
    public void runRules(final long aggregationPeriodTimestamp, final EventDistributor distributor) {
        for (final AggregationRule rule : list) {
            final List<Event> eventList = eventBuckets.remove(rule);

            if (eventList == null)
                continue;

            log.debug("there are {} event(s) to aggregate for rule {}", eventList.size(), rule);

            if (eventList.isEmpty())
                continue;

            try {
                final AggregationResultEvent result = rule.aggregate(aggregationPeriodTimestamp, eventList);

                log.debug("aggregation successful for rule {} => {}", rule, result);
                distributor.serialize(result);
            } catch (final Throwable x) {
                log.error("aggregation error", x);
            }
        }

        eventBuckets.clear();
    }


    /**
     * @param rule
     * @param e
     */
    private void appendToBucket(final AggregationRule rule, final Event e) {
        final List<Event> list = eventBuckets.get(rule);

        if (list != null) {
            list.add(e);
            return;
        }

        final List<Event> newEventListForRule = new ArrayList<Event>();

        newEventListForRule.add(e);
        eventBuckets.put(rule, newEventListForRule);
    }
}
