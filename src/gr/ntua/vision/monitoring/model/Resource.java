package gr.ntua.vision.monitoring.model;

/**
 * This specifies a monitored resource.
 */
public interface Resource extends JSONExchanged, Comparable<Resource>
{
	/**
	 * get the resource type.
	 * 
	 * @return the resource type.
	 */
	public String type();


	/**
	 * get the resource unit.
	 * 
	 * @return the resource unit.
	 */
	public String unit();


	/**
	 * get the resource value.
	 * 
	 * @return the resource value.
	 */
	public double value();


	/**
	 * set the resource value.
	 * 
	 * @param value
	 *            the resource value to set.
	 */
	public void setValue(double value);
}
