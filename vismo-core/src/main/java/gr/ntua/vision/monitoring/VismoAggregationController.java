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

	/**
	 * @param sock
	 */
	public VismoAggregationController(final EventDistributor stuff, final List<? extends AggregationRule> rulesList) {
		this.stuff = stuff;
		this.rulesList = rulesList;
	}

	@Override
	public void notify(Event e) {
		for (final AggregationRule rule : rulesList) {
			final String topic = e.topic();
			
			if (rule.matches(e)) {
				log.trace("matching rule {} for event {}", rule, e.getClass());
				appendToBucket(rule, e);
			} else if (topic != null && e.topic().equals("ResourceMap"))
				stuff.serialize(e);
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
			log.trace("rule {} has expired", rule);

			final List<Event> eventList = eventBuckets.remove(rule);

			if (eventList == null)
				continue;

			log.trace("there are {} event(s) to aggregate", eventList.size());

			if (eventList.isEmpty())
				continue;

			// FIXME: do we need tstart, tend?
			final AggregationResultEvent aggregatedResult = rule.aggregate(this.scheduledExecutionTime(), eventList);

			log.trace("aggregation successful for {}", aggregatedResult);
			stuff.serialize(aggregatedResult);
		}
	}

	@Override
	public void run() {
		try {
			performPendingOperations();
		} catch (Throwable x) {
			log.trace("performPendingOperations exception: ", x);
			x.printStackTrace();
		}
	}
}
