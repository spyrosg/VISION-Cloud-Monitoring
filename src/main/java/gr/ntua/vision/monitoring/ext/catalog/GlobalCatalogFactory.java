package gr.ntua.vision.monitoring.ext.catalog;

/**
 * This is the {@link Catalog} provider.
 */
public abstract class GlobalCatalogFactory {
    /** the cloud catalog used. */
    private static Catalog global_catalog = new InMemoryLocalCatalog();
    /** global catalog URL */
    private static String  global_url     = "http://localhost/vision-cloud/object-service/global-catalog/";


    /**
     * get the global catalog URL.
     * 
     * @return the global_url
     */
    public static String getGlobalURL() {
        return global_url;
    }


    /**
     * get the global catalog instance.
     * 
     * @return the instance.
     */
    public static Catalog globalCatalogInstance() {
        return global_catalog;
    }


    /**
     * set the global catalog URL.
     * 
     * @param global_url
     *            the global_url to set
     */
    public static void setGlobalURL(final String global_url) {
        GlobalCatalogFactory.global_url = global_url;
        global_catalog = new RemoteRESTCatalog( global_url, "application/global-catalog" );
    }
}
