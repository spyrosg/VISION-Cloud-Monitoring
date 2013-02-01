package helpers;

import gr.ntua.vision.monitoring.dispatch.EventBuilder;
import gr.ntua.vision.monitoring.dispatch.EventDispatcher;
import gr.ntua.vision.monitoring.events.VismoEvent;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class InMemoryEventDispatcher implements EventDispatcher {
    /**
     * 
     */
    private static class MyEvent extends VismoEvent {
        /**
         * Constructor.
         * 
         * @param dict
         */
        protected MyEvent(final Map<String, Object> dict) {
            super(dict);
        }


        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "#<MyEvent: " + dict() + ">";
        }
    }

    /***/
    private static final Logger    log = LoggerFactory.getLogger(InMemoryEventDispatcher.class);
    /***/
    private final EventBuilder     builder;
    /***/
    private final VismoRulesEngine engine;


    /**
     * Constructor.
     * 
     * @param engine
     */
    public InMemoryEventDispatcher(final VismoRulesEngine engine) {
        this.engine = engine;
        this.builder = new InMemoryEventBuilder(this);
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
        final MyEvent e = new MyEvent(map);

        log.debug("send map: {}", map);
        engine.receive(e);
    }
}
