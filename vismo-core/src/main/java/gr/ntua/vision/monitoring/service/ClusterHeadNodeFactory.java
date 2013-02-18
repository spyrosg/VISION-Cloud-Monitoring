package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sinks.EventSinksFactory;
import gr.ntua.vision.monitoring.sources.EventSources;
import gr.ntua.vision.monitoring.sources.EventSourcesFactory;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.util.List;


/**
 *
 */
public class ClusterHeadNodeFactory extends AbstractVismoServiceFactory {
    /**
     * @param conf
     * @param socketFactory
     * @param engine
     */
    public ClusterHeadNodeFactory(final VismoConfiguration conf, final ZMQFactory socketFactory, final VismoRulesEngine engine) {
        super(conf, socketFactory, engine);
    }


    /**
     * @see gr.ntua.vision.monitoring.service.AbstractVismoServiceFactory#getEventSinks()
     */
    @Override
    protected List< ? extends EventSink> getEventSinks() {
        return new EventSinksFactory(conf, socketFactory).buildForClusterHead();
    }


    /**
     * @see gr.ntua.vision.monitoring.service.AbstractVismoServiceFactory#getEventSources()
     */
    @Override
    protected EventSources getEventSources() {
        return new EventSourcesFactory(conf, socketFactory).buildForClusterHead();
    }
}
