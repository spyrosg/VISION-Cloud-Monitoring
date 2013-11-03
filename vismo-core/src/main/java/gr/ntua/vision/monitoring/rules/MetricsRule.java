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
    private static final int              PID   = new VismoVMInfo().getPID();
    /***/
    private static final String           TOPIC = "metrics";
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
        this.procMetric = new ProccessCPUMemoryMetric(PID);
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
        this.procMetric = new ProccessCPUMemoryMetric(PID);
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

        final Bandwidth b = hostBandwith.get();

        dict.put("inbound", b.inbound);
        dict.put("outbound", b.outbound);
        dict.put("cpu-load", hostCPU.get());

        final HostMemory hm = hostMemory.get();

        dict.put("memory-used", hm.used);
        dict.put("memory-total", hm.total);

        final HashMap<String, Object> proc = new HashMap<String, Object>();

        final CPUMemory cm = procMetric.get();

        proc.put("PID", PID);
        proc.put("memory-used", cm.memoryUsage);
        proc.put("cpu-load", cm.cpuLoad);

        proc.put("jvm", proc);

        return newAggregationEvent(dict);
    }


    /**
     * @param map
     * @return the aggregation results as a {@link MonitoringEvent}.
     */
    private MonitoringEvent newAggregationEvent(final Map<String, Object> map) {
        return factory.createEvent(map);
    }
}
