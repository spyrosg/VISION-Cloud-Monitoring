package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdditionRule implements AggregationRule {
	/***/
	private static final String DICT = "!dict";
	/***/
	private final String aggregationField;
	/***/
	private String resultField;
	/***/
	private static final Logger log = LoggerFactory.getLogger(AdditionRule.class);

	/**
	 * @param aggregationField
	 * @param resultField
	 */
	public AdditionRule(String aggregationField, final String resultField) {
		this.aggregationField = aggregationField;
		this.resultField = resultField;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AggregationResultEvent aggregate(List<? extends Event> eventList) {
		double sum = 0;

		// TODO: can we have the events sorted by timestamp? What guarantees do we have?
		// TODO: sort events by timestamp
		for (final Event e : eventList) {
			log.trace("aggregating event of class {}", e.getClass());

			final Object val = e.get(aggregationField);

			if (val == null) {
				log.trace("event with no appropriate field '{}'; skipping", aggregationField);
				continue;
			}

			sum += (Long) val;
		}

		final Event firstEvent = eventList.get(0);
		final Event lastEvent = eventList.get(eventList.size() - 1);
		@SuppressWarnings("rawtypes")
		final Map dict = (Map) lastEvent.get(DICT);

		dict.put("tStart", firstEvent.timestamp());
		dict.put("tEnd", lastEvent.timestamp());
		dict.put(resultField, sum);

		return new VismoAggregationResultEvent(dict);
	}

	@Override
	public boolean matches(Event e) {
		return true;
	}

	@Override
	public boolean hasExpired() {
		// TODO????
		return true;
	}

	@Override
	public String toString() {
		return "#<AdditionRule on field: " + aggregationField + ", result: " + resultField + ">";
	}
}
