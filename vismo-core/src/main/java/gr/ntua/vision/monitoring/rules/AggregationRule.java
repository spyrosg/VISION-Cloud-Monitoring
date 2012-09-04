package gr.ntua.vision.monitoring.rules;

import java.util.List;


import gr.ntua.vision.monitoring.events.Event;

/**
 * This is used to represent an operation on a sequence of events, over a period
 * of time.
 */
public interface AggregationRule {
	/**
	 * @param eventList
	 * @return
	 */
	AggregationResultEvent aggregate(List<? extends Event> eventList);

	/**
	 * @param e
	 * @return
	 */
	boolean matches(Event e);

	boolean hasExpired();
}
