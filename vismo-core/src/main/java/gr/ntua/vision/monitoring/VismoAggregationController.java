package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.AggregationResultEvent;
import gr.ntua.vision.monitoring.rules.AggregationRule;
import gr.ntua.vision.monitoring.zmq.VismoSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.json.simple.JSONObject;
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
	private final VismoSocket sock;
	/***/
	private static final Logger log = LoggerFactory.getLogger(VismoAggregationController.class);
	/** this is used to get a hold of the whole dict, for serialization reasons. */
	private static final String DICT_KEY = "!dict";

	/**
	 * @param sock
	 */
	public VismoAggregationController(final List<? extends AggregationRule> rulesList, VismoSocket sock) {
		this.rulesList = rulesList;
		this.sock = sock;
	}

	@Override
	public void notify(Event e) {
		for (final AggregationRule rule : rulesList)
			if (rule.matches(e)) {
				log.trace("matching rule {} for event of class {}", rule, e.getClass());
				appendToBucket(rule, e);
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
		for (final AggregationRule rule : rulesList)
			if (rule.hasExpired()) {
				log.trace("rule {} has expired", rule);

				final List<Event> eventList = eventBuckets.remove(rule);

				if (eventList == null)
					continue;

				log.trace("there are {} event(s) to aggregate for rule", eventList.size());

				if (eventList.isEmpty())
					continue;

				// FIXME: do we need tstart, tend?
				final AggregationResultEvent aggregatedResult = rule.aggregate(eventList);

				log.trace("aggregation successful for {}", aggregatedResult);
				sendEventDownThePipeline(aggregatedResult);
			}
	}

	/**
	 * @param e
	 */
	private void sendEventDownThePipeline(Event e) {
		log.trace("sending out aggregation result with event class: {}", e.getClass());
		@SuppressWarnings("rawtypes")
		final Map dict = (Map) e.get(DICT_KEY);

		sock.send(JSONObject.toJSONString(dict));
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
