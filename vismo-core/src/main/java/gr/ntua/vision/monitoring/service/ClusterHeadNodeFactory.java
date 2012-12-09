package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.rules.AccountingRule;
import gr.ntua.vision.monitoring.rules.CTORule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.EventSinks;
import gr.ntua.vision.monitoring.sinks.EventSinksFactory;
import gr.ntua.vision.monitoring.sources.EventSources;
import gr.ntua.vision.monitoring.sources.EventSourcesFactory;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;


/**
 *
 */
public class ClusterHeadNodeFactory extends CommonServiceFactory {
    /***/
    private final VismoConfiguration conf;
    /***/
    private final ZMQSockets         zmq;


    /**
     * @param conf
     * @param zmq
     */
    public ClusterHeadNodeFactory(final VismoConfiguration conf, final ZMQSockets zmq) {
        this.conf = conf;
        this.zmq = zmq;
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
        return new EventSinksFactory(conf, zmq).buildForClusterHead();
    }


    /**
     * @see gr.ntua.vision.monitoring.service.VismoServiceAbstractFactory#getEventSources()
     */
    @Override
    protected EventSources getEventSources() {
        return new EventSourcesFactory(conf, zmq).createforClusterHead();
    }


    /**
     * @param engine
     */
    private static void registerRules(final VismoRulesEngine engine) {
        // TODO: rename method

        final long ONE_MINUTE = 60 * 1000;
        final long THREE_SECONDS = 3 * 1000;

        new CTORule(engine, "cto-3-sec", THREE_SECONDS).submitTo(engine);
        new CTORule(engine, "cto-1-min", ONE_MINUTE).submitTo(engine);
        new AccountingRule(engine, ONE_MINUTE).submitTo(engine);
    }
}
