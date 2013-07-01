package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.VMInfo;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.resources.HttpEventResource;
import gr.ntua.vision.monitoring.resources.InternalMetricsResource;
import gr.ntua.vision.monitoring.resources.RulesResource;
import gr.ntua.vision.monitoring.resources.VersionResource;
import gr.ntua.vision.monitoring.rules.ClassPathRulesFactory;
import gr.ntua.vision.monitoring.rules.DefaultRuleBean;
import gr.ntua.vision.monitoring.rules.RuleBean;
import gr.ntua.vision.monitoring.rules.RulesStore;
import gr.ntua.vision.monitoring.rules.ThresholdRulesFactory;
import gr.ntua.vision.monitoring.rules.VismoRule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sources.EventSources;
import gr.ntua.vision.monitoring.web.WebAppBuilder;
import gr.ntua.vision.monitoring.web.WebServer;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to build up the vismo instance. Such an instance requires event sources, event sinks and a rules' engine.
 */
abstract class AbstractVismoServiceFactory implements ServiceFactory {
    /***/
    private static final Package       DEFAULT_RULES_PACKAGE = VismoRule.class.getPackage();
    /***/
    private static final Logger        log                   = LoggerFactory.getLogger(AbstractVismoServiceFactory.class);
    /***/
    private static final int           PORT                  = 9996;
    /***/
    protected final VismoConfiguration conf;
    /***/
    protected final ZMQFactory         socketFactory;


    /**
     * Constructor.
     * 
     * @param conf
     * @param socketFactory
     */
    public AbstractVismoServiceFactory(final VismoConfiguration conf, final ZMQFactory socketFactory) {
        this.conf = conf;
        this.socketFactory = socketFactory;
    }


    /**
     * @see gr.ntua.vision.monitoring.service.ServiceFactory#build(gr.ntua.vision.monitoring.VMInfo)
     */
    @Override
    public Service build(final VMInfo vminfo) throws IOException {
        log.info("building service...");

        final RulesStore store = new RulesStore();
        final VismoRulesEngine engine = new VismoRulesEngine(store);
        final EventSources sources = getEventSources();

        log.info("with {}", sources);

        engine.appendSinks(getEventSinks());

        log.info("subscribing sources to rules engine");
        sources.subscribeAll(engine);

        final WebServer server = buildWebServer(PORT, vminfo, store, engine);
        final VismoService service = new VismoService(vminfo, sources, engine, server);

        addDefaultServiceTasks(vminfo, service);
        log.info("take it from here");

        submitRules(engine);

        return service;
    }


    /**
     * Provides the event sinks for <code>this</code> service.
     * 
     * @return the list of {@link EventSink}s object, already configured.
     */
    protected abstract List< ? extends EventSink> getEventSinks();


    /**
     * Provides the event sources for <code>this</code> service.
     * 
     * @return an {@link EventSources} object, already configured.
     */
    protected abstract EventSources getEventSources();


    /**
     * This is used to submit rules to the rules engine at startup time.
     * 
     * @param engine
     *            the rules engine.
     */
    protected abstract void submitRules(final VismoRulesEngine engine);


    /**
     * Parse the configuration and load any rules.
     * 
     * @param engine
     */
    protected void submitRulesFromConf(final VismoRulesEngine engine) {
        final ClassPathRulesFactory rulesFactory = new ClassPathRulesFactory(engine, DEFAULT_RULES_PACKAGE);

        for (final String rule : conf.getStartupRules()) {
            final String[] fs = rule.split(":");

            try {
                final RuleBean bean = fs.length > 1 ? new DefaultRuleBean(fs[0], Long.valueOf(fs[1])) : new DefaultRuleBean(rule);

                rulesFactory.buildFrom(bean).submit();
            } catch (final Throwable x) {
                log.warn("cannot load rule specification " + rule + "; continuing", x);
            }
        }
    }


    /**
     * @param service
     * @param info
     */
    protected static void addDefaultServiceTasks(@SuppressWarnings("unused") final VMInfo info,
            @SuppressWarnings("unused") final VismoService service) {
        log.debug("adding default tasks");
    }


    /**
     * @param port
     * @param vminfo
     * @param store
     * @param engine
     * @return a configured {@link WebServer}.
     */
    private static WebServer buildWebServer(final int port, final VMInfo vminfo, final RulesStore store,
            final VismoRulesEngine engine) {
        final WebServer server = new WebServer(port);
        final RulesResource rulesResource = new RulesResource(new ThresholdRulesFactory(new ClassPathRulesFactory(engine,
                DEFAULT_RULES_PACKAGE), engine), store);
        final HttpEventResource eventSource = new HttpEventResource();
        final Application app = WebAppBuilder.buildFrom(rulesResource, new InternalMetricsResource(),
                                                        new VersionResource(vminfo), eventSource);

        eventSource.add(engine);

        return server.withWebAppAt(app, "/*");
    }
}
