package endtoend;

import gr.ntua.vision.monitoring.events.Event;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.UUID;

import org.json.simple.JSONObject;


/**
 * 
 */
public class ObsEvent implements Event {
    /***/
    private static final Random rng = new Random();
    /***/
    private final String        container;
    /***/
    private final long          contentSize;
    /***/
    private final String        id;
    /***/
    private final String        object;
    /***/
    private final String        operation;
    /***/
    private final String        status;
    /***/
    private final String        tenant;
    /***/
    private final long          timestamp;
    /***/
    private final double        transactionDuration;
    /***/
    private final double        transactionLatency;
    /***/
    private final double        transactionThroughput;
    /***/
    private final String        user;


    /**
     * Constructor.
     * 
     * @param tenant
     * @param user
     * @param container
     * @param object
     * @param operation
     * @param size
     */
    public ObsEvent(final String operation, final String tenant, final String user, final String container, final String object,
            final long size) {
        this.operation = operation;
        this.tenant = tenant;
        this.user = user;
        this.container = container;
        this.object = object;
        this.contentSize = size;
        this.timestamp = System.currentTimeMillis();
        this.id = UUID.randomUUID().toString();
        this.transactionDuration = rng.nextDouble();
        this.transactionLatency = rng.nextDouble();
        this.transactionThroughput = contentSize / this.transactionDuration;
        this.status = "SUCCESS";
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#get(java.lang.String)
     */
    @Override
    public Object get(@SuppressWarnings("unused") final String key) {
        throw new UnsupportedOperationException("niy");
    }


    /**
     * @return the container
     */
    public String getContainer() {
        return container;
    }


    /**
     * @return the contentSize
     */
    public long getContentSize() {
        return contentSize;
    }


    /**
     * @return the id
     */
    public String getId() {
        return id;
    }


    /**
     * @return the object
     */
    public String getObject() {
        return object;
    }


    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }


    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }


    /**
     * @return the transactionLatency
     */
    public double getTransactionLatency() {
        return transactionLatency;
    }


    /**
     * @return the transactionThroughput
     */
    public double getTransactionThroughput() {
        return transactionThroughput;
    }


    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#originatingIP()
     */
    @Override
    public InetAddress originatingIP() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#originatingService()
     */
    @Override
    public String originatingService() {
        return "fake-object-service";
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#timestamp()
     */
    @Override
    public long timestamp() {
        return timestamp;
    }


    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public JSONObject toJSON() {
        final JSONObject o = new JSONObject();

        o.put("id", id);
        o.put("timestamp", timestamp);
        o.put("operation", operation);
        o.put("tenant", tenant);
        o.put("user", user);
        o.put("container", container);
        o.put("object", object);
        o.put("content-size", String.valueOf(contentSize));
        o.put("transaction-duration", transactionDuration);
        o.put("transaction-latency", transactionLatency);
        o.put("transaction-throughput", transactionThroughput);
        o.put("originating-machine", "127.0.0.1");
        o.put("originating-service", originatingService());
        o.put("originating-cluster", "test");
        o.put("status", status);

        return o;
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#topic()
     */
    @Override
    public String topic() {
        throw new UnsupportedOperationException("niy");
    }
}
