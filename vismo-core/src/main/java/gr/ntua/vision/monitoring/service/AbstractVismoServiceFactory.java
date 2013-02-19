package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.VMInfo;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sources.EventSources;
import gr.ntua.vision.monitoring.threading.JVMStatusReportTask;
import gr.ntua.vision.monitoring.threading.PingGroupTask;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to build up the vismo instance. Such an instance requires event sources, event sinks and a rules' engine.
 */
abstract class AbstractVismoServiceFactory implements ServiceFactory {
    /***/
    private static final Logger        log        = LoggerFactory.getLogger(AbstractVismoServiceFactory.class);
    /***/
    private static final long          ONE_MINUTE = 60 * 1000;
    /***/
    protected final VismoConfiguration conf;
    /***/
    protected final ZMQFactory         socketFactory;
    /***/
    private final VismoRulesEngine     engine;


    /**
     * Constructor.
     * 
     * @param conf
     * @param socketFactory
     * @param engine
     */
    public AbstractVismoServiceFactory(final VismoConfiguration conf, final ZMQFactory socketFactory,
            final VismoRulesEngine engine) {
        this.conf = conf;
        this.socketFactory = socketFactory;
        this.engine = engine;
    }


    /**
     * @see gr.ntua.vision.monitoring.service.ServiceFactory#build(gr.ntua.vision.monitoring.VMInfo)
     */
    @Override
    public Service build(final VMInfo vminfo) throws IOException {
        log.info("building service...");

        final EventSources sources = getEventSources();

        log.info("with {}", sources);

        engine.appendSinks(getEventSinks());

        log.info("subscribing sources to rules engine");
        sources.subscribeAll(engine);

        final VismoService service = new VismoService(vminfo, sources, engine, new RulesPropagationManager(engine, 9996));

        addDefaultServiceTasks(vminfo, service);
        log.info("take it from here");

        return service;
    }


    /**
     * @param service
     * @param info
     * @throws UnknownHostException
     */
    protected void addDefaultServiceTasks(final VMInfo info, final VismoService service) throws UnknownHostException {
        log.debug("adding default tasks");
        service.addTask(new JVMStatusReportTask(ONE_MINUTE));
        service.addTask(new PingGroupTask(conf, info.getVersion()));
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
}
