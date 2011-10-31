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
	 * get the resource value (in KilloBytes).
	 * 
	 * @return the resource value.
	 */
	public double value();


	/**
	 * get the container name.
	 * 
	 * @return the name.
	 */
	public String containerName();


	/**
	 * get the object name.
	 * 
	 * @return the name.
	 */
	public String objectName();


	/**
	 * get the tenant name.
	 * 
	 * @return the name.
	 */
	public String tenantName();


	/**
	 * set the resource value.
	 * 
	 * @param value
	 *            the resource value to set.
	 */
	public void setValue(double value);
}
