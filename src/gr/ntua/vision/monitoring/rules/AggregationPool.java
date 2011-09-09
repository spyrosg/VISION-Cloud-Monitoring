package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.model.Event;

import java.util.UUID;

import com.google.common.base.Function;


/**
 * An event aggregation pool. Pools aggregate events and create more compact ones. They are responsible for performing the action
 * on the aggregated event when the conditions permit so.
 */
public class AggregationPool extends Thread
{
	/** the pool's id. */
	public final UUID					id;
	/** the minimum count of events in a group. */
	private final int					minCount;
	/** the maximum count of events in a group. */
	private final int					maxCount;
	/** the maximum time difference between the most recent and the oldest event in the group. */
	private final long					timeWindow;
	/** the action to perform with aggregated events. */
	private final Function<Event, Void>	action;


	/**
	 * c/tor.
	 * 
	 * @param id
	 * @param action
	 * @param minCount
	 * @param maxCount
	 * @param timeWindow
	 */
	AggregationPool(UUID id, Function<Event, Void> action, int minCount, int maxCount, long timeWindow)
	{
		this.id = id;
		this.action = action;
		this.minCount = minCount;
		this.maxCount = maxCount;
		this.timeWindow = timeWindow;
	}


	public void push(Event event)
	{
	}
}
