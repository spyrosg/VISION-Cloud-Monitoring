package gr.ntua.vision.monitoring.policy;

import gr.ntua.vision.monitoring.VMInfo;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.rules.ClassPathRulesFactory;
import gr.ntua.vision.monitoring.rules.DefaultRuleBean;
import gr.ntua.vision.monitoring.rules.RuleBean;
import gr.ntua.vision.monitoring.rules.VismoRule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.service.CloudHeadNodeFactory;
import gr.ntua.vision.monitoring.service.ClusterHeadNodeFactory;
import gr.ntua.vision.monitoring.service.Service;
import gr.ntua.vision.monitoring.service.VismoService;
import gr.ntua.vision.monitoring.service.WorkerNodeFactory;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;


/**
 * Provides a {@link VismoService} instance using the configuration.
 */
public class StaticConfigPolicy implements NodePolicy {
    /***/
    private static final Package     DEFAULT_RULES_PACKAGE = VismoRule.class.getPackage();
    /** the log target. */
    private static final Logger      log                   = LoggerFactory.getLogger(StaticConfigPolicy.class);
    /** the configuration object. */
    private final VismoConfiguration conf;
    /***/
    private final VismoRulesEngine   engine;


    /**
     * Constructor.
     * 
     * @param conf
     *            the configuration object.
     * @param engine
     */
    public StaticConfigPolicy(final VismoConfiguration conf, final VismoRulesEngine engine) {
        this.conf = conf;
        this.engine = engine;
    }


    /**
     * @throws IOException
     * @see gr.ntua.vision.monitoring.policy.NodePolicy#build(gr.ntua.vision.monitoring.VMInfo)
     */
    @Override
    public Service build(final VMInfo vminfo) throws IOException {
        logConfig(vminfo);

        if (hostIsCloudHead(vminfo) || hostIsClusterHead(vminfo))
            loadStartupRules(engine, DEFAULT_RULES_PACKAGE);

        final ZMQFactory socketFactory = new ZMQFactory(new ZContext());

        if (hostIsCloudHead(vminfo))
            return new CloudHeadNodeFactory(conf, socketFactory, engine).build(vminfo);
        else if (hostIsClusterHead(vminfo))
            return new ClusterHeadNodeFactory(conf, socketFactory, engine).build(vminfo);
        else
            return new WorkerNodeFactory(conf, socketFactory, engine).build(vminfo);
    }


    /**
     * Check whether local-host is the cluster head (according to the configuration).
     * 
     * @param vminfo
     * @return <code>true</code> when local-host is a cloud head, <code>false</code> otherwise.
     */
    private boolean hostIsCloudHead(final VMInfo vminfo) {
        return conf.isIPCloudHead(vminfo.getAddress().getHostAddress());
    }


    /**
     * Check whether local-host is a cluster head (according to the configuration).
     * 
     * @param vminfo
     * @return <code>true</code> when local-host is a cluster head, <code>false</code> otherwise.
     */
    private boolean hostIsClusterHead(final VMInfo vminfo) {
        return conf.isIPClusterHead(vminfo.getAddress().getHostAddress());
    }


    /**
     * Parse the configuration and load any rules.
     * 
     * @param engine
     * @param pkg
     */
    private void loadStartupRules(final VismoRulesEngine engine, final Package pkg) {
        final ClassPathRulesFactory rulesFactory = new ClassPathRulesFactory(engine, pkg);

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
     * @param vminfo
     */
    private void logConfig(final VMInfo vminfo) {
        log.info("is cluster head? {}", hostIsClusterHead(vminfo));
        log.info("is cloud head? {}", hostIsCloudHead(vminfo));
    }
}
