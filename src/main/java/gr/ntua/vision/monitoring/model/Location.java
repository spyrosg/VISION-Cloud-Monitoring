package gr.ntua.vision.monitoring.model;

/**
 * The location of an event generation or target. Note that depending on the event type and actual location, some of the
 * information contained here is meaningless. Therefore there are cases in which the methods will return <code>null</code>s to
 * indicate that no relevant data are associated.
 */
public interface Location extends JSONExchanged {
    /**
     * get the hostname.
     * 
     * @return the hostname.
     */
    public String hostname();


    /**
     * get the network address.
     * 
     * @return the network address.
     */
    public String netAddress();


    /**
     * get the service name.
     * 
     * @return the service name.
     */
    public String service();


    /**
     * get the ID of the tenant, in the context of which this event took place. Not all events happen in the context of a tenant
     * and for those that don't this method returns <code>null</code>.
     * 
     * @return the tenant ID.
     */
    public String tenantID();


    /**
     * get the user ID.
     * 
     * @return the user ID.
     */
    public String userID();
}
