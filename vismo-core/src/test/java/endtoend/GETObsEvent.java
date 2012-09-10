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
    public GETObsEvent(String tenant, String user, String container, String object, long size) {
        super("GET", tenant, user, container, object, size);
    }
}
