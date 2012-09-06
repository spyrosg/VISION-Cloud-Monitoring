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
	private final Map<AggregationRule, List<Event>> eventBuckets = new HashMap<AggregationRule, List<Event>>();
	/***/
	private final List<? extends AggregationRule> rulesList;
	/***/
	private final EventDistributor stuff;
	/***/
	private static final Logger log = LoggerFactory.getLogger(VismoAggregationController.class);
	/***/
	private final long aggregationPeriod;

	/**
	 * @param sock
	 */
	public VismoAggregationController(final EventDistributor stuff, final List<? extends AggregationRule> rulesList,
			final long aggregationPeriod) {
		this.stuff = stuff;
		this.rulesList = rulesList;
		this.aggregationPeriod = aggregationPeriod;
	}

	@Override
	public void notify(Event e) {
		for (final AggregationRule rule : rulesList) {
			if (rule.matches(e)) {
				appendToBucket(rule, e);
			} else {
				stuff.serialize(e);
			}
		}
	}

	/**
	 * @param rule
	 * @param e
	 */
	private void appendToBucket(AggregationRule rule, Event e) {
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
			stuff.serialize(aggregatedResult);
		}
	}

	@Override
	public void run() {
		log.trace("starting aggregation");

		final long start = System.currentTimeMillis();

		try {
			performPendingOperations();
		} catch (Throwable x) {
			log.trace("performPendingOperations exception: ", x);
		}

		log.trace("ending aggregation in {} seconds", (System.currentTimeMillis() - start) / 1000.0);
	}
}
