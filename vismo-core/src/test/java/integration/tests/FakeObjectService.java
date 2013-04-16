package integration.tests;

import gr.ntua.vision.monitoring.dispatch.EventBuilder;
import gr.ntua.vision.monitoring.dispatch.EventDispatcher;

import java.util.Random;


/**
 * This is used to emulate the Vision Object Service. The operations specified can send events to the locally running vismo
 * instance.
 */
public class FakeObjectService {
    /***/
    public enum Operation {
        /***/
        DELETE("delete"),
        /***/
        GET("read"),
        /***/
        PUT("write");

        /***/
        public final String type;


        /**
         * @param type
         */
        private Operation(final String type) {
            this.type = type;
        }
    }


    /***/
    public enum Status {
        /***/
        ERROR,
        /***/
        SUCCESS;
    }

    /** the max request/response duration (in seconds). */
    private static final int      MAX_DURATION = 100;
    /** the max request content size (in bytes). */
    private static final int      MAX_SIZE     = 1000000;
    /***/
    private final EventDispatcher dispatcher;
    /***/
    private final Random          rng;


    /**
     * Constructor.
     * 
     * @param dispatcher
     */
    public FakeObjectService(final EventDispatcher dispatcher) {
        this(dispatcher, new Random());
    }


    /**
     * @param dispatcher
     * @param rng
     */
    public FakeObjectService(final EventDispatcher dispatcher, final Random rng) {
        this.dispatcher = dispatcher;
        this.rng = rng;
    }


    /**
     * @param tenant
     * @param user
     * @param container
     * @param object
     * @return an obs delete event.
     */
    public EventBuilder delEvent(final String tenant, final String user, final String container, final String object) {
        return randomEvent(Operation.DELETE, tenant, user, container, object, Status.SUCCESS);
    }


    /**
     * @param tenant
     * @param user
     * @param container
     * @param object
     * @return an obs get event
     */
    public EventBuilder getEvent(final String tenant, final String user, final String container, final String object) {
        return randomEvent(Operation.GET, tenant, user, container, object, Status.SUCCESS);
    }


    /**
     * @param tenant
     * @param user
     * @param container
     * @param object
     * @return an obs put event
     */
    public EventBuilder putEvent(final String tenant, final String user, final String container, final String object) {
        return randomEvent(Operation.PUT, tenant, user, container, object, Status.SUCCESS);
    }


    /**
     * @param maxSize
     * @return a rnd.
     */
    private int rand(final int maxSize) {
        return rng.nextInt(maxSize + 1);
    }


    /**
     * @param op
     * @param tenant
     * @param user
     * @param container
     * @param object
     * @param st
     * @return an event
     */
    private EventBuilder randomEvent(final Operation op, final String tenant, final String user, final String container,
            final String object, final Status st) {
        final int contentSize = rand(MAX_SIZE);
        final double duration = rand(MAX_DURATION);
        final double latency = rand((int) duration);
        final double throughput = contentSize * 1.0 / duration;

        return dispatcher.newEvent().field("operation", op.toString()).field("tenant", tenant).field("user", user)
                .field("container", container).field("object", object).field("status", st.toString())
                .field("content-size", contentSize).field("transaction-latency", latency).field("transaction-duration", duration)
                .field("transaction-throughput", throughput).field("type", op.type);
    }
}
