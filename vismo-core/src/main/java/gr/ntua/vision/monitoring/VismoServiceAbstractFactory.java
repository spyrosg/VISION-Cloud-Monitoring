package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.EventSinks;
import gr.ntua.vision.monitoring.sources.EventSources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
/**
 *
 */
abstract class VismoServiceAbstractFactory {
    /***/
    private static final Logger log = LoggerFactory.getLogger(VismoServiceAbstractFactory.class);


    /**
     * @param vminfo
     * @return
     */
    public VismoService build(final VMInfo vminfo) {
        final EventSources sources = newEventSources();
        final VismoRulesEngine engine = new VismoRulesEngine(newEventSinks());

        sources.subscribeAll(engine);
        boostrap(engine);

        return new VismoService(vminfo, sources, engine);
    }


    /**
     * @param engine
     */
    protected abstract void boostrap(VismoRulesEngine engine);


    /**
     * Provides the event sinks for <code>this</code> service.
     * 
     * @return an {@link EventSinks} object, already configured.
     */
    protected abstract EventSinks newEventSinks();


    /**
     * Provides the event sources for <code>this</code> service.
     * 
     * @return an {@link EventSources} object, already configured.
     */
    protected abstract EventSources newEventSources();
}
