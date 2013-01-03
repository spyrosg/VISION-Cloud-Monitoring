package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.VMInfo;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.EventSinks;
import gr.ntua.vision.monitoring.sources.EventSources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to build up the vismo instance. Such an instance requires event sources, event sinks and a rules' engine.
 */
abstract class VismoServiceAbstractFactory {
    /***/
    private static final Logger log = LoggerFactory.getLogger(VismoServiceAbstractFactory.class);


    /**
     * Build and configure the {@link VismoService} instance.
     * 
     * @param vminfo
     * @return the {@link VismoService} object ready to run.
     */
    public Service build(final VMInfo vminfo) {
        final EventSources sources = getEventSources();
        final VismoRulesEngine engine = new VismoRulesEngine(getEventSinks());

        sources.subscribeAll(engine);
        boostrap(engine);

        final VismoService service = new VismoService(vminfo, sources, engine);

        bootstrap(service);

        return service;
    }


    /**
     * This is used to configure the rules' engine; mainly to add rules before the actual execution starts.
     * 
     * @param engine
     *            the rules' engine.
     */
    protected abstract void boostrap(VismoRulesEngine engine);


    /**
     * This is used to further configure the vismo service just before it's run.
     * 
     * @param service
     *            the vismo service.
     */
    protected abstract void bootstrap(VismoService service);


    /**
     * Provides the event sinks for <code>this</code> service.
     * 
     * @return an {@link EventSinks} object, already configured.
     */
    protected abstract EventSinks getEventSinks();


    /**
     * Provides the event sources for <code>this</code> service.
     * 
     * @return an {@link EventSources} object, already configured.
     */
    protected abstract EventSources getEventSources();
}
