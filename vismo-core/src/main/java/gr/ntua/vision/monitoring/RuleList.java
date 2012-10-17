package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.AggregationResult;
import gr.ntua.vision.monitoring.rules.AggregationRule;
import gr.ntua.vision.monitoring.rules.RuleAggregationListener;

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
    /** the actual rule list. */
    private final ArrayList<AggregationRule>        list         = new ArrayList<AggregationRule>();


    /**
     * @param rule
     */
    public void add(final AggregationRule rule) {
        log.debug("adding rule {}", rule);
        list.add(rule);
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
     * @param listener
     */
    public void runRules(final RuleAggregationListener listener) {
        for (final AggregationRule rule : list) {
            final List<Event> eventList = eventBuckets.remove(rule);

            if (eventList == null || eventList.isEmpty())
                continue;

            log.debug("there are {} event(s) to aggregate for rule {}", eventList.size(), rule);

            AggregationResult result = null;

            listener.startAggregation(rule);

            try {
                result = rule.aggregate(eventList);
            } catch (final Throwable x) {
                log.error("aggregation error", x);
            } finally {
                listener.endAggregation(rule, result);
            }
        }

        eventBuckets.clear();
    }


    /**
     * @param rule
     * @param e
     */
    private void appendToBucket(final AggregationRule rule, final Event e) {
        final List<Event> bucketList = eventBuckets.get(rule);

        if (bucketList != null) {
            bucketList.add(e);
            return;
        }

        final List<Event> newEventListForRule = new ArrayList<Event>();

        newEventListForRule.add(e);
        eventBuckets.put(rule, newEventListForRule);
    }
}
