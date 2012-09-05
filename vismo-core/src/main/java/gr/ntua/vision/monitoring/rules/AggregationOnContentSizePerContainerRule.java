package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggregationOnContentSizePerContainerRule implements AggregationRule {
	/***/
	private static final String DICT = "!dict";
	/***/
	private final String aggregationField;
	/***/
	private final String newField;
	/***/
	private final String operation;
	/***/
	private static final Logger log = LoggerFactory.getLogger(AggregationOnContentSizePerContainerRule.class);
	/***/
	private static final String SPECIAL_FIELD = "transaction-duration";

	/**
	 * @param aggregationField
	 * @param resultField
	 */
	public AggregationOnContentSizePerContainerRule(final String operation, String aggregationField, final String resultField) {
		this.operation = operation;
		this.aggregationField = aggregationField;
		this.newField = resultField;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AggregationResultEvent aggregate(List<? extends Event> eventList) {
		double sum = 0;

		// TODO: can we have the events sorted by timestamp? What guarantees do we have?
		// TODO: sort events by timestamp
		for (final Event e : eventList) {
			log.trace("aggregating event for {}", e.getClass());

			final Object val = e.get(aggregationField);

			if (val == null) {
				log.trace("event with no appropriate field '{}'; skipping", aggregationField);
				continue;
			}

			try {
				sum += (Long) val;
			} catch (ClassCastException x) {
				log.trace("expecting field '{}' of type {} ...", aggregationField, Long.class);
				log.trace("but got value {} of type {}", val, val.getClass());
				log.trace("", x);
			}
		}

		return new VismoAggregationResultEvent(appendNewField(eventList, sum));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map appendNewField(List<? extends Event> eventList, double sum) {
		final Event firstEvent = eventList.get(0);
		final Event lastEvent = eventList.get(eventList.size() - 1);
		final Map dict = (Map) lastEvent.get(DICT);

		// FIXME: these should be gotten off the timer
		dict.put("tStart", firstEvent.timestamp());
		dict.put("tEnd", lastEvent.timestamp());
		dict.put(newField, sum);
		dict.put("objects", getObjectList(eventList));

		return dict;
	}

	private Object getObjectList(List<? extends Event> eventList) {
		final List<String> l = new ArrayList<String>(eventList.size());

		for (final Event e : eventList)
			l.add((String) e.get("object"));

		return l;
	}

	@Override
	public boolean matches(Event e) {
		final String op = (String) e.get("operation");

		// FIXME: add a field for events coming from vismo_dispatch
		return e.get(SPECIAL_FIELD) != null && op.equals(operation);
	}

	@Override
	public boolean hasExpired() {
		// TODO????
		return true;
	}

	@Override
	public String toString() {
		return "#<AggregationOnContentSizeRule[" + operation + "] on field: " + aggregationField + ", with new field '"
				+ newField + "'>";
	}
}
