package unit.tests;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.EventHandlerTask;
import gr.ntua.vision.monitoring.notify.Registry;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;


/**
 *
 */
public class InMemoryEventRegistry implements Registry {
    /***/
    private final ArrayList<EventHandler> handlers;


    /**
     * Constructor.
     */
    public InMemoryEventRegistry() {
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
     * @param noEvents
     */
    public void pushEvents(final int noEvents) {
        for (final EventHandler handler : handlers)
            for (int i = 0; i < noEvents; ++i)
                handler.handle(new MonitoringEvent() {
                    @Override
                    public Object get(final String key) {
                        // TODO Auto-generated method stub
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
                        // TODO Auto-generated method stub
                        return null;
                    }


                    @Override
                    public long timestamp() {
                        return System.currentTimeMillis();
                    }


                    @Override
                    public String topic() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                });
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.Registry#register(java.lang.String, gr.ntua.vision.monitoring.notify.EventHandler)
     */
    @Override
    public EventHandlerTask register(final String topic, final EventHandler handler) {
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
