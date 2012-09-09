package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * This is an event generated only by Vision Cloud's Object Service.
 */
public class ObsEvent implements Event {
    /**
     * @see gr.ntua.vision.monitoring.events.Event#get(java.lang.String)
     */
    @Override
    public Object get(final String key) {
        throw new UnsupportedOperationException("niy");
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#originatingIP()
     */
    @Override
    public InetAddress originatingIP() throws UnknownHostException {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#originatingService()
     */
    @Override
    public String originatingService() {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#timestamp()
     */
    @Override
    public long timestamp() {
        // TODO Auto-generated method stub
        return 0;
    }


    /**
     * @see gr.ntua.vision.monitoring.events.Event#topic()
     */
    @Override
    public String topic() {
        // TODO Auto-generated method stub
        return null;
    }

}
