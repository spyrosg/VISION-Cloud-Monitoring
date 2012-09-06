package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.List;

/**
 * This is used to represent an operation on a sequence of events, over a period of time.
 */
public interface AggregationRule {
	/**
	 * @param eventList
	 * @return
	 */
	AggregationResultEvent aggregate(final long aggregationStartTime, List<? extends Event> eventList);

	/**
	 * @param e
	 * @return
	 */
	boolean matches(Event e);
}
