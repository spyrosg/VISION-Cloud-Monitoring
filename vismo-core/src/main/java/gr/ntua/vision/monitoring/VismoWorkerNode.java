package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sources.EventSources;


/**
 * 
 */
public class VismoWorkerNode extends VismoService {
    /***/
    private final VismoRulesEngine engine;


    /**
     * Constructor.
     * 
     * @param vminfo
     * @param sources
     * @param engine
     */
    public VismoWorkerNode(final VMInfo vminfo, final EventSources sources, final VismoRulesEngine engine) {
        super(vminfo, sources);
        this.engine = engine;
    }
}
