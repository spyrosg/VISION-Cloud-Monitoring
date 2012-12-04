package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sources.EventSources;


/**
 * 
 */
public class VismoClusterHeadNode extends VismoService {
    /***/
    private final VismoRulesEngine engine;


    /**
     * Constructor.
     * 
     * @param info
     * @param sources
     * @param engine
     */
    public VismoClusterHeadNode(final VMInfo info, final EventSources sources, final VismoRulesEngine engine) {
        super(info, sources);
        this.engine = engine;
    }


    /**
     * @see gr.ntua.vision.monitoring.udp.UDPListener#halt()
     */
    @Override
    public void halt() {
        super.halt();
        engine.halt();
    }
}
