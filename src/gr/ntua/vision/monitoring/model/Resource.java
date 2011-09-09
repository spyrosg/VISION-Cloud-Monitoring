package gr.ntua.vision.monitoring.model;

/**
 * This specifies a monitored resource.
 */
public interface Resource
{
	/**
	 * get the resource name.
	 * 
	 * @return the resource name.
	 */
	public String resourceName();


	/**
	 * get the type of the measurement objects. This method always returns the box type of a java builtin type.
	 * 
	 * @return the type of the measurement objects.
	 */
	public Class< ? > measurementType();
}
