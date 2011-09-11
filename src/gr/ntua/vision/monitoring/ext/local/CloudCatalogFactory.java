package gr.ntua.vision.monitoring.ext.local;

/**
 * This is the {@link Catalog} provider.
 */
public abstract class CloudCatalogFactory
{
	/** the cloud catalog used. */
	private static Catalog	cloud_catalog	= new InMemoryLocalCatalog();


	/**
	 * get the cloud catalog instance.
	 * 
	 * @return the instance.
	 */
	public static Catalog cloudCatalogInstance()
	{
		return cloud_catalog;
	}
}
