package gr.ntua.vision.monitoring.ext.catalog;

/**
 * This is the {@link Catalog} provider.
 */
public abstract class LocalCatalogFactory
{
	/** the local catalog used. */
	private static Catalog	local_catalog	= new InMemoryLocalCatalog();
	/** local catalog URL */
	private static String	local_url		= "http://localhost/vision-cloud/local-catalog/";


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
	 * set the local catalog URL.
	 * 
	 * @param local_url
	 *            the local_url to set
	 */
	public static void setLocalURL(String local_url)
	{
		LocalCatalogFactory.local_url = local_url;
		local_catalog = new RemoteRESTCatalog( local_url, "application/local-catalog" );
	}


	/**
	 * get the local catalog URL.
	 * 
	 * @return the local_url
	 */
	public static String getLocalURL()
	{
		return local_url;
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
		return new RemoteRESTCatalog( clusterID, "application/local-catalog" );
	}
}
