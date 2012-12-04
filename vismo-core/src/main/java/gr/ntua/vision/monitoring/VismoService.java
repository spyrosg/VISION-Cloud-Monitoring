package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.sources.EventSources;
import gr.ntua.vision.monitoring.udp.UDPListener;

import java.util.List;


/**
 * Basic support for the various vismo services.
 */
public abstract class VismoService implements UDPListener, Service {
    /***/
    private final EventSources sources;
    /***/
    private final VMInfo       vminfo;


    /**
     * Constructor.
     * 
     * @param vminfo
     * @param sources
     */
    public VismoService(final VMInfo vminfo, final EventSources sources) {
        this.vminfo = vminfo;
        this.sources = sources;
    }


    /**
     * @see gr.ntua.vision.monitoring.udp.UDPListener#collectStatus(java.util.List)
     */
    @Override
    public void collectStatus(final List<String> statuses) {
        statuses.add(String.valueOf(vminfo.getPID()));
    }


    /**
     * @see gr.ntua.vision.monitoring.udp.UDPListener#halt()
     */
    @Override
    public void halt() {
        sources.halt();
    }


    /**
     * 
     */
    @Override
    public void start() {
        sources.start();
    }
}
