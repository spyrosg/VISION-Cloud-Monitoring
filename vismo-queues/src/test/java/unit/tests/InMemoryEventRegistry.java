package unit.tests;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.EventHandlerTask;
import gr.ntua.vision.monitoring.notify.Registry;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;


/**
 *
 */
public class InMemoryEventRegistry implements Registry {
    /***/
    final String                          topic;
    /***/
    private final ArrayList<EventHandler> handlers;


    /**
     * Constructor.
     * 
     * @param topic
     */
    public InMemoryEventRegistry(final String topic) {
        this.topic = topic;
        this.handlers = new ArrayList<EventHandler>(1);
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.Registry#halt()
     */
    @Override
    public void halt() {
        // Nothing to do
    }


    /**
     * Generate some dummy monitoring events.
     * 
     * @param noEvents
     *            the number of events to generate.
     */
    public void pushEvents(final int noEvents) {
        for (final EventHandler handler : handlers)
            for (int i = 0; i < noEvents; ++i)
                handler.handle(new MonitoringEvent() {
                    @Override
                    public Object get(final String key) {
                        return null;
                    }


                    @Override
                    public InetAddress originatingIP() throws UnknownHostException {
                        return InetAddress.getLocalHost();
                    }


                    @Override
                    public String originatingService() {
                        return "in-memory-event-registry";
                    }


                    @Override
                    public String serialize() {
                        final Map<String, Object> dict = new HashMap<String, Object>();

                        dict.put("timestamp", timestamp());
                        dict.put("topic", topic());
                        dict.put("originating-service", originatingService());

                        return JSONObject.toJSONString(dict);
                    }


                    @Override
                    public long timestamp() {
                        return System.currentTimeMillis();
                    }


                    @Override
                    public String topic() {
                        return topic;
                    }
                });
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.Registry#register(java.lang.String, gr.ntua.vision.monitoring.notify.EventHandler)
     */
    @Override
    public EventHandlerTask register(final String topic1, final EventHandler handler) {
        handlers.add(handler);
        return null;
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.Registry#registerToAll(gr.ntua.vision.monitoring.notify.EventHandler)
     */
    @Override
    public EventHandlerTask registerToAll(final EventHandler handler) {
        throw new UnsupportedOperationException();
    }
}
