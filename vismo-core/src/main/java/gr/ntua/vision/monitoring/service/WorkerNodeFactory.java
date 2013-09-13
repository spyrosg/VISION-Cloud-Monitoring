package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sinks.EventSinksFactory;
import gr.ntua.vision.monitoring.sources.EventSources;
import gr.ntua.vision.monitoring.sources.EventSourcesFactory;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.util.List;


/**
 * This is used to build the vismo worker instance.
 */
public class WorkerNodeFactory extends AbstractVismoServiceFactory {
    /**
     * @param conf
     * @param socketFactory
     */
    public WorkerNodeFactory(final VismoConfiguration conf, final ZMQFactory socketFactory) {
        super(conf, socketFactory);
    }


    /**
     * @see gr.ntua.vision.monitoring.service.AbstractVismoServiceFactory#getEventSinks()
     */
    @Override
    protected List< ? extends EventSink> getEventSinks() {
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
