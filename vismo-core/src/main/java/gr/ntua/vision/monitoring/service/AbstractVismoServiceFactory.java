package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.VMInfo;
import gr.ntua.vision.monitoring.rules.RulesStore;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.sinks.EventSinks;
import gr.ntua.vision.monitoring.sources.EventSources;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to build up the vismo instance. Such an instance requires event sources, event sinks and a rules' engine.
 */
abstract class AbstractVismoServiceFactory implements ServiceFactory {
    /***/
    private static final Logger log = LoggerFactory.getLogger(AbstractVismoServiceFactory.class);


    /**
     * @see gr.ntua.vision.monitoring.service.ServiceFactory#build(gr.ntua.vision.monitoring.VMInfo)
     */
    @Override
    public Service build(final VMInfo vminfo) throws IOException {
        log.info("building service...");

        final EventSources sources = getEventSources();

        log.info("with {}", sources);

        final EventSinks sinks = getEventSinks();

        log.info("with {}", sinks);

        final VismoRulesEngine engine = new VismoRulesEngine(new RulesStore(), sinks);

        log.info("subscribing sources to rules engine");
        sources.subscribeAll(engine);
        log.info("bootstrapping rules engine");
        boostrap(engine);

        final RulesPropagationManager rulesManager = new RulesPropagationManager(engine, 9996);
        final VismoService service = new VismoService(vminfo, sources, engine, rulesManager);

        log.info("bootstrapping {}", service);
        bootstrap(service);
        log.info("take it from here");

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
