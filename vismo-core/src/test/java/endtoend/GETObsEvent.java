package endtoend;

/**
 * 
 */
public class GETObsEvent extends ObsEvent {
    /**
     * Constructor.
     * 
     * @param tenant
     * @param user
     * @param container
     * @param object
     * @param size
     */
    public GETObsEvent(final String tenant, final String user, final String container, final String object, final long size) {
        super("GET", tenant, user, container, object, size);
    }
}
