package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.VMInfo;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sources.EventSources;
import gr.ntua.vision.monitoring.threading.PeriodicTask;
import gr.ntua.vision.monitoring.udp.UDPListener;

import java.util.List;
import java.util.Timer;


/**
 * Basic support for the various vismo services.
 */
public class VismoService implements UDPListener, Service {
    /***/
    private final VismoRulesEngine engine;
    /***/
    private final EventSources     sources;
    /** the timer object. */
    private final Timer            timer = new Timer();
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
     * Schedule a period task with the service.
     * 
     * @param task
     *            the task.
     */
    public void addTask(final PeriodicTask task) {
        task.scheduleWith(timer);
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
        timer.cancel();
        sources.halt();
        engine.halt();
    }


    /**
     * Start the service, i.e, start receiving events and running rules.
     */
    @Override
    public void start() {
        sources.start();
    }
}
