package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.model.Event;

import java.util.UUID;

import com.google.common.base.Function;


/**
 * This specifies a class able to handle rule actions.
 */
public interface ActionHandler
{
	/**
	 * ensure the existence of the specified pool and create it if it doesn't exist.
	 * 
	 * @param pool
	 *            the pool ID.
	 * @param action
	 *            event handling action.
	 * @param maxCount
	 *            the maximum event count in a group.
	 * @param timeWindow
	 *            the maximum time difference between the most recent and the oldest event in the group.
	 * @param fields
	 *            the aggregation key fields. All events in a group have the same values in those.
	 * @return the pool.
	 */
	public AggregationPool pool(UUID pool, Function<Event, Void> action, int maxCount, long timeWindow, CheckedField... fields);


	/**
	 * store the given event under the specified key.
	 * 
	 * @param event
	 *            event to store.
	 * @param key
	 *            the key to store the event under.
	 */
	public void store(Event event, String key);


	/**
	 * transmit the given event.
	 * 
	 * @param event
	 *            event to transmit.
	 * @param pushURL
	 *            the URL of the push REST service.
	 */
	public void transmit(Event event, String pushURL);
}
