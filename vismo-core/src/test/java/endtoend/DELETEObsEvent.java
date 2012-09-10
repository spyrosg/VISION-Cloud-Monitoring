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
    public DELETEObsEvent(String tenant, String user, String container, String object, long size) {
        super("DELETE", tenant, user, container, object, size);
    }
}
