package gr.ntua.vision.monitoring.ext.local;

/**
 * This is the {@link Catalog} provider.
 */
public abstract class LocalCatalogFactory
{
	/** the local catalog used. */
	private static Catalog	local_catalog	= new InMemoryLocalCatalog();


	/**
	 * get the local catalog instance.
	 * 
	 * @return the instance.
	 */
	public static Catalog localCatalogInstance()
	{
		return local_catalog;
	}


	/**
	 * get the local catalog instance.
	 * 
	 * @param clusterID
	 *            ID of the cluster.
	 * @return the instance.
	 */
	public static Catalog localCatalogInstance(String clusterID)
	{
		return local_catalog;
	}
}
