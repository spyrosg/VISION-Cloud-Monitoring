package gr.ntua.vision.monitoring.probe;

/**
 * This is aiding in creating {@link Probe} instances by hiding their implementation details (ie. their types).
 */
public abstract class ProbeFactory
{
	/**
	 * create a new {@link Probe} instance.
	 * 
	 * @param name
	 *            the probe's name.
	 * @param cmdparts
	 *            the command parts of the probe.
	 * @param attrSep
	 *            the attribute list separator character.
	 * @param kvSep
	 *            the key value separator character.
	 * @param execPeriod
	 *            the execution period.
	 * @param execTimeout
	 *            the execution timeout.
	 * @param storeKey
	 *            the storage key.
	 * @param fail
	 *            the string used as a response when this probe fails.
	 * @param retries
	 *            the number of retries, before considering the probe's execution failed.
	 * @return the instance created.
	 * @throws Exception
	 *             - if the probe failed to get created.
	 */
	public static Probe create(String name, String[] cmdparts, Character attrSep, Character kvSep, Integer execPeriod,
			Integer execTimeout, String storeKey, String fail, Integer retries) throws Exception
	{
		return new DefaultProbe( name, cmdparts, attrSep, kvSep, execPeriod, execTimeout, storeKey, fail, retries );
	}
}
