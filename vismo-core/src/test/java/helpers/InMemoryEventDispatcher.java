package helpers;

import gr.ntua.vision.monitoring.dispatch.EventBuilder;
import gr.ntua.vision.monitoring.dispatch.EventDispatcher;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;

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
    private static final Logger     log = LoggerFactory.getLogger(InMemoryEventDispatcher.class);
    /***/
    private final EventBuilder      builder;
    /***/
    private final VismoRulesEngine  engine;
    /***/
    private final VismoEventFactory factory;
    /***/
    private final String            serviceName;


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
        this.factory = new VismoEventFactory();
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

        final MonitoringEvent e = factory.createEvent(map);

        log.debug("send map: {}", e);
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
