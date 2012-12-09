package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.EventSinks;
import gr.ntua.vision.monitoring.sinks.EventSinksFactory;
import gr.ntua.vision.monitoring.sources.EventSources;
import gr.ntua.vision.monitoring.sources.EventSourcesFactory;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

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
    private final ZMQSockets         zmq;


    /**
     * @param conf
     * @param zmq
     */
    public WorkerNodeFactory(final VismoConfiguration conf, final ZMQSockets zmq) {
        this.conf = conf;
        this.zmq = zmq;
    }


    /**
     * @see gr.ntua.vision.monitoring.service.VismoServiceAbstractFactory#boostrap(gr.ntua.vision.monitoring.rules.VismoRulesEngine)
     */
    @Override
    protected void boostrap(final VismoRulesEngine engine) {
        registerDefaultRules(engine);
    }


    /**
     * @see gr.ntua.vision.monitoring.service.VismoServiceAbstractFactory#getEventSinks()
     */
    @Override
    protected EventSinks getEventSinks() {
        return new EventSinksFactory(conf, zmq).buildForWorker();
    }


    /**
     * @see gr.ntua.vision.monitoring.service.VismoServiceAbstractFactory#getEventSources()
     */
    @Override
    protected EventSources getEventSources() {
        return new EventSourcesFactory(conf, zmq).createforWorker();
    }
}
