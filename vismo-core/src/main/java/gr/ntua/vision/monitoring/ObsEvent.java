package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;


/**
 * This is an event generated only by Vision Cloud's Object Service.
 */
public class ObsEvent implements Event {
    /***/
    private final String id;
    /***/
    private final long   timestamp;


    /**
     * Constructor.
     */
    private ObsEvent() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#get(java.lang.String)
     */
    @Override
    public Object get(final String key) {
        throw new UnsupportedOperationException("niy");
    }


    /**
     * @return the id
     */
    public String getId() {
        return id;
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
        return "object-service";
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#timestamp()
     */
    @Override
    public long timestamp() {
        return timestamp;
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#topic()
     */
    @Override
    public String topic() {
        throw new UnsupportedOperationException("niy");
    }
}
