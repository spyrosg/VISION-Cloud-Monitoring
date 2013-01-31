package helpers;

import gr.ntua.vision.monitoring.dispatch.EventBuilder;
import gr.ntua.vision.monitoring.dispatch.EventDispatcher;


/**
 * 
 */
class InMemoryEventDispatcher implements EventDispatcher {
    /**
     * Constructor.
     */
    public InMemoryEventDispatcher() {
    }


    /**
     * @see gr.ntua.vision.monitoring.dispatch.EventDispatcher#newEvent()
     */
    @Override
    public EventBuilder newEvent() {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * @see gr.ntua.vision.monitoring.dispatch.EventDispatcher#send()
     */
    @Override
    public void send() {
        // TODO Auto-generated method stub
    }
}
