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
	 * <p>
	 * The aggregated events are tracked for and packed together in groups of at least <code>minCount</code> and at most
	 * <code>maxCount</code> elements, all occurring in a time window with maximum duration <code>timeWindow</code>. All of these
	 * parameters can be disabled, by setting it to a negative number, but at least one must be strictly positive.
	 * 
	 * @param pool
	 *            the pool ID.
	 * @param action
	 *            event handling action.
	 * @param minCount
	 *            the minimum count of events in a group.
	 * @param maxCount
	 *            the maximum count of events in a group.
	 * @param timeWindow
	 *            the maximum time difference between the most recent and the oldest event in the group.
	 * @return the pool.
	 */
	public AggregationPool pool(UUID pool, Function<Event, Void> action, int minCount, int maxCount, long timeWindow);


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
