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
    public PUTObsEvent(final String tenant, final String user, final String container, final String object, final long size) {
        super("PUT", tenant, user, container, object, size);
    }
}
