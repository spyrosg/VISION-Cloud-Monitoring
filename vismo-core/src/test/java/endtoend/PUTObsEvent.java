package endtoend;

/**
 * 
 */
public class PUTObsEvent extends ObsEvent {
    /**
     * Constructor.
     *
     * @param tenant
     * @param user
     * @param container
     * @param object
     * @param size
     */
    public PUTObsEvent(String tenant, String user, String container, String object, long size) {
        super("PUT", tenant, user, container, object, size);
    }
}
