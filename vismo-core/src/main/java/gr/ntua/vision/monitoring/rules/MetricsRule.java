package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.metrics.HostBandwithMetric;
import gr.ntua.vision.monitoring.metrics.HostBandwithMetric.Bandwidth;
import gr.ntua.vision.monitoring.metrics.HostCPULoadMetric;
import gr.ntua.vision.monitoring.metrics.HostMemory;
import gr.ntua.vision.monitoring.metrics.LinuxHostMemoryMetric;
import gr.ntua.vision.monitoring.metrics.ProccessCPUMemoryMetric;
import gr.ntua.vision.monitoring.metrics.ProccessCPUMemoryMetric.CPUMemory;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class MetricsRule extends PeriodicRule {
    /***/
    private static final VismoVMInfo      vminfo  = new VismoVMInfo();
    /***/
    private static final String           TOPIC = "metrics";
    /***/
    private static final String           SERVICE = "vismo";
    /***/
    private final VismoEventFactory       factory;
    /***/
    private final HostBandwithMetric      hostBandwith;
    /***/
    private final HostCPULoadMetric       hostCPU;
    /***/
    private final LinuxHostMemoryMetric   hostMemory;
    /***/
    private final ProccessCPUMemoryMetric procMetric;


    /**
     * @param engine
     * @param period
     * @throws FileNotFoundException
     */
    public MetricsRule(final VismoRulesEngine engine, final long period) throws FileNotFoundException {
        super(engine, period);
        this.factory = new VismoEventFactory();
        this.hostBandwith = new HostBandwithMetric();
        this.hostCPU = new HostCPULoadMetric();
        this.hostMemory = new LinuxHostMemoryMetric();
        this.procMetric = new ProccessCPUMemoryMetric(vminfo.getPID());
    }


    /**
     * @param engine
     * @param period
     * @param id
     * @throws FileNotFoundException
     */
    public MetricsRule(final VismoRulesEngine engine, final long period, final String id) throws FileNotFoundException {
        super(engine, period, id);
        this.factory = new VismoEventFactory();
        this.hostBandwith = new HostBandwithMetric();
        this.hostCPU = new HostCPULoadMetric();
        this.hostMemory = new LinuxHostMemoryMetric();
        this.procMetric = new ProccessCPUMemoryMetric(vminfo.getPID());
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
     */
    @Override
    public void performWith(final MonitoringEvent c) {
        // NOP, nothing to do with incoming events
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.PeriodicRule#aggregate(java.util.List, long, long)
     */
    @Override
    protected MonitoringEvent aggregate(final List<MonitoringEvent> list, final long tStart, final long tEnd) {
        final HashMap<String, Object> dict = new HashMap<String, Object>();

        dict.put("topic", TOPIC);
        dict.put("originating-service", SERVICE);
        dict.put("timestamp", System.currentTimeMillis());
        dict.put("originating-machine", vminfo.getAddress().getHostAddress());

        final Bandwidth b = hostBandwith.get();

        dict.put("inbound", b.inbound);
        dict.put("outbound", b.outbound);
        dict.put("cpu-load", hostCPU.get());

        final HostMemory hm = hostMemory.get();

        dict.put("memory-used", hm.used);
        dict.put("memory-total", hm.total);

        final HashMap<String, Object> proc = new HashMap<String, Object>();

        final CPUMemory cm = procMetric.get();

        proc.put("PID", vminfo.getPID());
        proc.put("memory-used", cm.memoryUsage);
        proc.put("cpu-load", cm.cpuLoad);

        dict.put("jvm", proc);

        return newAggregationEvent(dict);
    }

    /**
     * @see gr.ntua.vision.monitoring.rules.PeriodicRule#shouldAlwaysRun()
     */
    @Override
    protected boolean shouldAlwaysRun() {
        return true;
    }

    /**
     * @param map
     * @return the aggregation results as a {@link MonitoringEvent}.
     */
    private MonitoringEvent newAggregationEvent(final Map<String, Object> map) {
        return factory.createEvent(map);
    }
}
