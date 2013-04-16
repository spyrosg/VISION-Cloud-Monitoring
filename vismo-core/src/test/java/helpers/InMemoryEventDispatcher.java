package helpers;

import gr.ntua.vision.monitoring.dispatch.EventBuilder;
import gr.ntua.vision.monitoring.dispatch.EventDispatcher;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class InMemoryEventDispatcher implements EventDispatcher {
    /***/
    private static class MyEvent implements MonitoringEvent {
        /***/
        private final Map<String, Object> dict;


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
    private static final Logger    log = LoggerFactory.getLogger(InMemoryEventDispatcher.class);
    /***/
    private final EventBuilder     builder;
    /***/
    private final VismoRulesEngine engine;
    /***/
    private final String           serviceName;


    /**
     * Constructor.
     * 
     * @param engine
     * @param serviceName
     */
    public InMemoryEventDispatcher(final VismoRulesEngine engine, final String serviceName) {
        this.engine = engine;
        this.builder = new InMemoryEventBuilder(this);
        this.serviceName = serviceName;
    }


    /**
     * @see gr.ntua.vision.monitoring.dispatch.EventDispatcher#newEvent()
     */
    @Override
    public EventBuilder newEvent() {
        return builder;
    }


    /**
     * @see gr.ntua.vision.monitoring.dispatch.EventDispatcher#send()
     */
    @Override
    public void send() {
        throw new Error("unused / unimplemented");

    }


    /**
     * @param map
     */
    void send(final HashMap<String, Object> map) {
        addBasicFields(map);

        final MyEvent e = new MyEvent(map);

        log.debug("send map: {}", map);
        engine.receive(e);
    }


    /**
     * @param map
     *            the event as represented with a {@link Map}.
     */
    private void addBasicFields(final Map<String, Object> map) {
        map.put("timestamp", System.currentTimeMillis());
        map.put("originating-machine", "127.0.0.1");
        map.put("originating-service", serviceName);
        map.put("originating-cluster", "in-memory-cluster");
        map.put("id", getEventId());
    }


    /**
     * @return an id for the event.
     */
    private static String getEventId() {
        return UUID.randomUUID().toString();
    }
}
