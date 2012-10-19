package endtoend;

/**
 * 
 */
public class DELETEObsEvent extends ObsEvent {
    /**
     * Constructor.
     * 
     * @param tenant
     * @param user
     * @param container
     * @param object
     * @param size
     */
    public DELETEObsEvent(final String tenant, final String user, final String container, final String object, final long size) {
        super("DELETE", tenant, user, container, object, size);
    }
}
