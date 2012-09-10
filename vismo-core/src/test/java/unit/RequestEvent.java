package unit;

import gr.ntua.vision.monitoring.events.Event;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class RequestEvent implements Event {
    private final long   size;
    private final String topic;


    public RequestEvent(final String topic, final long size) {
        this.topic = topic;
        this.size = size;
    }


    @Override
    public Object get(final String key) {
        if (key.equals("content-size"))
            return size;

        return null;
    }


    @Override
    public InetAddress originatingIP() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }


    @Override
    public String originatingService() {
        return "object-service";
    }


    @Override
    public long timestamp() {
        return 0;
    }


    @Override
    public String topic() {
        return topic;
    }
}
