package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.AggregationResultEvent;
import gr.ntua.vision.monitoring.rules.AggregationRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is SO GONNA DIE after the f2f.
 */
public class VismoAggregationController extends TimerTask implements EventListener {
    /***/
    private static final Logger                     log          = LoggerFactory.getLogger(VismoAggregationController.class);
    /***/
    private final long                              aggregationPeriod;
    /***/
    private final EventDistributor                  distributor;
    /***/
    private final Map<AggregationRule, List<Event>> eventBuckets = new HashMap<AggregationRule, List<Event>>();
    /***/
    private final List< ? extends AggregationRule>  rulesList;


    /**
     * Constructor.
     * 
     * @param distributor
     * @param rulesList
     * @param aggregationPeriod
     */
    public VismoAggregationController(final EventDistributor distributor, final List< ? extends AggregationRule> rulesList,
            final long aggregationPeriod) {
        this.distributor = distributor;
        this.rulesList = rulesList;
        this.aggregationPeriod = aggregationPeriod;
    }


    /**
     * @see gr.ntua.vision.monitoring.EventListener#notify(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void notify(final Event e) {
        for (final AggregationRule rule : rulesList)
            if (rule.matches(e))
                appendToBucket(rule, e);

        distributor.serialize(e);
    }


    /**
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        final long periodInSeconds = this.aggregationPeriod / 1000;

        log.trace("timer of {} seconds expired, starting aggregation", periodInSeconds);

        final long start = System.currentTimeMillis();

        try {
            performPendingOperations();
        } catch (final Throwable x) {
            log.trace("performPendingOperations exception: ", x);
        }

        log.trace("aggregation end for {} seconds timer, in {} seconds", periodInSeconds,
                  (System.currentTimeMillis() - start) / 1000.0);
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


    /**
	 * 
	 */
    private void performPendingOperations() {
        for (final AggregationRule rule : rulesList) {
            final List<Event> eventList = eventBuckets.remove(rule);

            if (eventList == null)
                continue;

            log.debug("there are {} event(s) to aggregate for rule {}", eventList.size(), rule);

            if (eventList.isEmpty())
                continue;

            // FIXME: do we need tstart, tend?
            final AggregationResultEvent aggregatedResult = rule.aggregate(this.scheduledExecutionTime() - aggregationPeriod,
                                                                           eventList);

            log.debug("aggregation successful for {}", aggregatedResult);
            distributor.serialize(aggregatedResult);
        }
    }
}
