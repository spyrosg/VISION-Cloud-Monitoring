package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sinks.EventSinksFactory;
import gr.ntua.vision.monitoring.sources.EventSourceListener;
import gr.ntua.vision.monitoring.sources.EventSources;
import gr.ntua.vision.monitoring.sources.EventSourcesFactory;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.util.List;


/**
 *
 */
public class CloudHeadNodeFactory extends AbstractVismoServiceFactory {
    /**
     * @param conf
     * @param socketFactory
     */
    public CloudHeadNodeFactory(final VismoConfiguration conf, final ZMQFactory socketFactory) {
        super(conf, socketFactory);
    }


    /**
     * @see gr.ntua.vision.monitoring.service.AbstractVismoServiceFactory#getEventSinks()
     */
    @Override
    protected List< ? extends EventSink> getEventSinks() {
        return new EventSinksFactory(conf, socketFactory).buildForCloudHead();
    }


    /**
     * @see gr.ntua.vision.monitoring.service.AbstractVismoServiceFactory#getEventSources(gr.ntua.vision.monitoring.sources.EventSourceListener)
     */
    @Override
    protected EventSources getEventSources(final EventSourceListener listener) {
        return new EventSourcesFactory(conf, socketFactory, listener).buildForCloudHead();
    }
}
