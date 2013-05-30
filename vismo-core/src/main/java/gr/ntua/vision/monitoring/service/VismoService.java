package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.VMInfo;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sources.EventSources;
import gr.ntua.vision.monitoring.threading.PeriodicTask;
import gr.ntua.vision.monitoring.udp.UDPListener;
import gr.ntua.vision.monitoring.web.WebServer;

import java.util.List;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to start and halt the various subsystems.
 */
public class VismoService implements Service, UDPListener {
    /***/
    private static final Logger    log   = LoggerFactory.getLogger(VismoService.class);
    /***/
    private final VismoRulesEngine engine;
    /***/
    private final WebServer        httpServer;
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
     * @param httpServer
     */
    public VismoService(final VMInfo vminfo, final EventSources sources, final VismoRulesEngine engine, final WebServer httpServer) {
        this.vminfo = vminfo;
        this.sources = sources;
        this.engine = engine;
        this.httpServer = httpServer;
    }


    /**
     * Schedule a period task with the service.
     * 
     * @param task
     *            the task.
     */
    public void addTask(final PeriodicTask task) {
        log.debug("scheduling {}", task);
        task.scheduleWith(timer);
    }


    /**
     * @see gr.ntua.vision.monitoring.udp.UDPListener#collectStatus(java.util.List)
     */
    @Override
    public void collectStatus(final List<String> statuses) {
        statuses.add(String.valueOf(vminfo.getPID()));
        statuses.add(vminfo.getVersion());
    }


    /**
     * @see gr.ntua.vision.monitoring.service.Service#halt()
     */
    @Override
    public void halt() {
        log.debug("halting subsystems");
        timer.cancel();
        sources.halt();
        engine.halt();

        try {
            httpServer.stop();
        } catch (final Exception e) {
            log.warn("stopping http server", e);
            // NOP
        }
    }


    /**
     * Start the service, i.e, start receiving events and running rules.
     */
    @Override
    public void start() {
        log.info("this is {} with pid {}", this, vminfo.getPID());
        log.debug("starting subsystems");
        sources.start();

        try {
            httpServer.start();
        } catch (final Exception e) {
            log.warn("starting http server", e);
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<VismoService @ " + vminfo.getAddress().getHostAddress() + ">";
    }
}
