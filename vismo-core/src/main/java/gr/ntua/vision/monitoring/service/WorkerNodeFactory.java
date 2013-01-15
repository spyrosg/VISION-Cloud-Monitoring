package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.sinks.EventSinks;
import gr.ntua.vision.monitoring.sinks.EventSinksFactory;
import gr.ntua.vision.monitoring.sources.EventSources;
import gr.ntua.vision.monitoring.sources.EventSourcesFactory;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to build the vismo worker instance.
 */
public class WorkerNodeFactory extends CommonServiceFactory {
    /** the log target. */
    private static final Logger      log = LoggerFactory.getLogger(WorkerNodeFactory.class);
    /** the configuration object. */
    private final VismoConfiguration conf;
    /***/
    private final ZMQFactory         socketFactory;


    /**
     * @param conf
     * @param socketFactory
     */
    public WorkerNodeFactory(final VismoConfiguration conf, final ZMQFactory socketFactory) {
        this.conf = conf;
        this.socketFactory = socketFactory;
    }


    /**
     * @see gr.ntua.vision.monitoring.service.AbstractVismoServiceFactory#getEventSinks()
     */
    @Override
    protected EventSinks getEventSinks() {
        return new EventSinksFactory(conf, socketFactory).buildForWorker();
    }


    /**
     * @see gr.ntua.vision.monitoring.service.AbstractVismoServiceFactory#getEventSources()
     */
    @Override
    protected EventSources getEventSources() {
        return new EventSourcesFactory(conf, socketFactory).buildForWorker();
    }
}
