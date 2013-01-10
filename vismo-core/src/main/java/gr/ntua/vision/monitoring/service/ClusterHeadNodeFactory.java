package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.rules.AccountingRule;
import gr.ntua.vision.monitoring.rules.CTORule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.EventSinks;
import gr.ntua.vision.monitoring.sinks.EventSinksFactory;
import gr.ntua.vision.monitoring.sources.EventSources;
import gr.ntua.vision.monitoring.sources.EventSourcesFactory;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;


/**
 *
 */
public class ClusterHeadNodeFactory extends CommonServiceFactory {
    /***/
    private final VismoConfiguration conf;
    /***/
    private final ZMQFactory         socketFactory;


    /**
     * @param conf
     * @param socketFactory
     */
    public ClusterHeadNodeFactory(final VismoConfiguration conf, final ZMQFactory socketFactory) {
        this.conf = conf;
        this.socketFactory = socketFactory;
    }


    /**
     * @see gr.ntua.vision.monitoring.service.VismoServiceAbstractFactory#boostrap(gr.ntua.vision.monitoring.rules.VismoRulesEngine)
     */
    @Override
    protected void boostrap(final VismoRulesEngine engine) {
        super.boostrap(engine);
        registerRules(engine);
    }


    /**
     * @see gr.ntua.vision.monitoring.service.VismoServiceAbstractFactory#getEventSinks()
     */
    @Override
    protected EventSinks getEventSinks() {
        return new EventSinksFactory(conf, socketFactory).buildForClusterHead();
    }


    /**
     * @see gr.ntua.vision.monitoring.service.VismoServiceAbstractFactory#getEventSources()
     */
    @Override
    protected EventSources getEventSources() {
        return new EventSourcesFactory(conf, socketFactory).buildForClusterHead();
    }


    /**
     * @param engine
     */
    private static void registerRules(final VismoRulesEngine engine) {
        // TODO: rename method

        final long ONE_MINUTE = 60 * 1000;
        final long THREE_SECONDS = 3 * 1000;

        new CTORule(engine, "cto-3-sec", THREE_SECONDS).submit();
        new CTORule(engine, "cto-1-min", ONE_MINUTE).submit();
        new AccountingRule(engine, ONE_MINUTE).submit();
    }
}
