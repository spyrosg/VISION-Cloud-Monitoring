package gr.ntua.vision.monitoring.ext.local;

/**
 * This is the {@link LocalCatalog} provider.
 */
public abstract class LocalCatalogFactory
{
	/** the local catalog used. */
	private static LocalCatalog	local_catalog	= new InMemoryLocalCatalog();


	/**
	 * get the local catalog instance.
	 * 
	 * @return the instance.
	 */
	public static LocalCatalog localCatalogInstance()
	{
		return local_catalog;
	}
}
