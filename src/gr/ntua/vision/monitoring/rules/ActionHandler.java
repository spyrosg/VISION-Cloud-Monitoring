package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.model.Event;

import java.util.UUID;


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
	 * @param minCount
	 *            the minimum count of events in a group.
	 * @param maxCount
	 *            the maximum count of events in a group.
	 * @param timeWindow
	 *            the maximum time difference between the most recent and the oldest event in the group.
	 */
	public void ensurePool(UUID pool, int minCount, int maxCount, long timeWindow);


	/**
	 * request the aggregation of the events, matching the given prototype (as specified in DRL).
	 * 
	 * @param prototype
	 *            event prototype, obtained from the DRL.
	 * @param pool
	 *            aggregation pool.
	 * @param key
	 *            the key to store the event groups under.
	 */
	public void aggregateNStore(Event prototype, UUID pool, String key);


	/**
	 * request the push of the events, matching the given prototype (as specified in DRL). Events are pushed at the time they
	 * arrive. No aggregation takes place.
	 * 
	 * @param prototype
	 *            event prototype, obtained from the DRL.
	 * @param pushURL
	 *            the URL of the push REST service.
	 */
	public void pushEvent(Event prototype, String pushURL);


	/**
	 * request the push of the events, matching the given prototype (as specified in DRL).
	 * 
	 * @param prototype
	 *            event prototype, obtained from the DRL.
	 * @param pool
	 *            aggregation pool.
	 * @param pushURL
	 *            the URL of the push REST service.
	 */
	public void aggregateNPush(Event prototype, UUID pool, String pushURL);
}
