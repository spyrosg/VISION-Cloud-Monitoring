package gr.ntua.vision.monitoring.probe;

import gr.ntua.vision.monitoring.util.Pair;

import java.util.List;


/**
 * This defines a monitoring probe. Probes require to be invoked in regular intervals, to collect data and provide them to their
 * caller.
 */
public interface Probe extends Runnable
{
	/**
	 * get the probe's name.
	 * 
	 * @return the name.
	 */
	public String name();


	/**
	 * get the store key this should log to.
	 * 
	 * @return the key.
	 */
	public String storeKey();


	/**
	 * this gets the desired execution period of this probe. It is measured in seconds.
	 * 
	 * @return the number of seconds.
	 */
	public int period();


	/**
	 * get the timestamp when this {@link #run()} for the last time.
	 * 
	 * @return a UNIX time.
	 */
	public long lastCollectionTime();


	/**
	 * get the data this probe collected the last time it {@link #run()}.
	 * 
	 * @return the data.
	 */
	public List<Pair<String, Object>> lastCollected();
}
