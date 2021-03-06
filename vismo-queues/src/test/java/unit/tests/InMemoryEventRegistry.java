package unit.tests;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.EventHandlerTask;
import gr.ntua.vision.monitoring.notify.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public class InMemoryEventRegistry implements Registry {
    /***/
    final String                          topic;
    /***/
    private final VismoEventFactory       factory = new VismoEventFactory();
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
     * Push the event to the handlers.
     * 
     * @param map
     */
    public void pushEvent(final Map<String, Object> map) {
        for (final EventHandler handler : handlers) {
            map.put("timestamp", System.currentTimeMillis());
            map.put("originating-machine", "localhost");
            handler.handle(factory.createEvent(map));
        }
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
                handler.handle(newObsGETEvent());
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
        handlers.add(handler);
        return null;
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.Registry#unregister(gr.ntua.vision.monitoring.notify.EventHandler)
     */
    @Override
    public void unregister(final EventHandler handler) {
        // NOP
    }


    /**
     * @return a {@link MonitoringEvent}.
     */
    private MonitoringEvent newObsGETEvent() {
        final HashMap<String, Object> m = new HashMap<String, Object>();

        m.put("timestamp", System.currentTimeMillis());
        // m.put("topic", topic);
        m.put("originating-machine", "localhost");
        m.put("originating-service", "object_service");
        m.put("operation", "GET");
        m.put("status", "SUCCESS");

        return factory.createEvent(m);
    }
}
