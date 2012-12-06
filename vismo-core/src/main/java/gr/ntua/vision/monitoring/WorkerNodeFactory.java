package gr.ntua.vision.monitoring;

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
public class WorkerNodeFactory extends DefaultRulesFactory {
    /***/
    private static final Logger      log = LoggerFactory.getLogger(WorkerNodeFactory.class);
    /***/
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
     * @see gr.ntua.vision.monitoring.VismoServiceAbstractFactory#boostrap(gr.ntua.vision.monitoring.rules.VismoRulesEngine)
     */
    @Override
    protected void boostrap(final VismoRulesEngine engine) {
        registerDefaultRules(engine);
    }


    /**
     * @see gr.ntua.vision.monitoring.VismoServiceAbstractFactory#newEventSinks()
     */
    @Override
    protected EventSinks newEventSinks() {
        return new EventSinksFactory(conf, zmq).createForWorker();
    }


    /**
     * @see gr.ntua.vision.monitoring.VismoServiceAbstractFactory#newEventSources()
     */
    @Override
    protected EventSources newEventSources() {
        return new EventSourcesFactory(conf, zmq).createforWorker();
    }
}
