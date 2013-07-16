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


/**
 *
 */
public class InMemoryEventRegistry implements Registry {
    /***/
    public static class MyEvent implements MonitoringEvent {
        /***/
        public final Map<String, Object> dict;


        /**
         * Constructor.
         * 
         * @param dict
         */
        public MyEvent(final Map<String, Object> dict) {
            this.dict = dict;
        }


        /**
         * @see gr.ntua.vision.monitoring.events.MonitoringEvent#get(java.lang.String)
         */
        @Override
        public Object get(final String key) {
            return dict.get(key);
        }


        /**
         * @see gr.ntua.vision.monitoring.events.MonitoringEvent#originatingIP()
         */
        @Override
        public InetAddress originatingIP() throws UnknownHostException {
            return InetAddress.getByName((String) dict.get("originating-machine"));
        }


        /**
         * @see gr.ntua.vision.monitoring.events.MonitoringEvent#originatingService()
         */
        @Override
        public String originatingService() {
            return (String) dict.get("originating-service");
        }


        /**
         * @see gr.ntua.vision.monitoring.events.MonitoringEvent#serialize()
         */
        @Override
        public String serialize() {
            return toString();
        }


        /**
         * @see gr.ntua.vision.monitoring.events.MonitoringEvent#timestamp()
         */
        @Override
        public long timestamp() {
            return (Long) dict.get("timestamp");
        }


        /**
         * @see gr.ntua.vision.monitoring.events.MonitoringEvent#topic()
         */
        @Override
        public String topic() {
            return null;
        }


        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "#<MyEvent: " + dict + ">";
        }
    }

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
            for (int i = 0; i < noEvents; ++i) {
                final HashMap<String, Object> m = new HashMap<String, Object>();

                m.put("timestamp", System.currentTimeMillis());
                m.put("topic", topic);
                m.put("originating-service", "localhost");

                handler.handle(new MyEvent(m));
            }
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


    /**
     * @see gr.ntua.vision.monitoring.notify.Registry#unregister(gr.ntua.vision.monitoring.notify.EventHandler)
     */
    @Override
    public void unregister(final EventHandler handler) {
        // NOP
    }
}
