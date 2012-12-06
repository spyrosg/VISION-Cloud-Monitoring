package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sources.EventSources;
import gr.ntua.vision.monitoring.udp.UDPListener;

import java.util.List;


/**
 * Basic support for the various vismo services.
 */
public class VismoService implements UDPListener, Service {
    /***/
    private final VismoRulesEngine engine;
    /***/
    private final EventSources     sources;
    /***/
    private final VMInfo           vminfo;


    /**
     * Constructor.
     * 
     * @param vminfo
     * @param sources
     * @param engine
     */
    public VismoService(final VMInfo vminfo, final EventSources sources, final VismoRulesEngine engine) {
        this.vminfo = vminfo;
        this.sources = sources;
        this.engine = engine;
    }


    /**
     * @see gr.ntua.vision.monitoring.udp.UDPListener#collectStatus(java.util.List)
     */
    @Override
    public void collectStatus(final List<String> statuses) {
        // FIXME: it's incomplete, should be collecting statuses from various threads.
        statuses.add(String.valueOf(vminfo.getPID()));
    }


    /**
     * @see gr.ntua.vision.monitoring.udp.UDPListener#halt()
     */
    @Override
    public void halt() {
        sources.halt();
        engine.halt();
    }


    /**
     * 
     */
    @Override
    public void start() {
        sources.start();
    }
}
