package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class provides for the low level responsibilities for a rule that runs periodically, over a list of monitoringEvents.
 */
public abstract class PeriodicRule extends TimerTask implements VismoRule {
    /***/
    private static final Logger                log        = LoggerFactory.getLogger(PeriodicRule.class);
    /***/
    protected final ArrayList<MonitoringEvent> eventsList = new ArrayList<MonitoringEvent>();
    /***/
    private final VismoRulesEngine             engine;
    /***/
    private final String                       id;
    /** the rule's period, in milliseconds. */
    private long                               period;


    /**
     * Constructor.
     * 
     * @param engine
     * @param period
     *            the rule's period, in milliseconds.
     */
    public PeriodicRule(final VismoRulesEngine engine, final long period) {
        this(engine, period, getId());
    }


    /**
     * Constructor.
     * 
     * @param engine
     * @param period
     *            the rule's period, in milliseconds.
     * @param id
     *            the rule's id.
     */
    public PeriodicRule(final VismoRulesEngine engine, final long period, final String id) {
        if (period <= 0)
            throw new IllegalArgumentException("rule period should be > 0");

        this.engine = engine;
        this.period = period;
        this.id = id != null ? id : getId();
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#id()
     */
    @Override
    public String id() {
        return id;
    }


    /**
     * Run the aggregation method over all collected monitoring events. The result, if any, will be passed back to the rules
     * engine. Upon completion, the event list will be cleared.
     * 
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        // the end of the aggregation period marks the start of the aggregation application
        final long lastAggregationPeriodEnd = System.currentTimeMillis();
        // the start of the aggregation period
        final long lastAggregationPeriodStart = lastAggregationPeriodEnd - period;

        if (eventsList.isEmpty() && !shouldAlwaysRun())
            return;

        log.debug("{} will aggregate over {} events", getClass().getSimpleName(), eventsList.size());

        final long start = System.currentTimeMillis();

        try {
            final ArrayList<MonitoringEvent> copy = new ArrayList<MonitoringEvent>(eventsList);

            send(aggregate(copy, lastAggregationPeriodStart, lastAggregationPeriodEnd));
        } finally {
            final long dur = System.currentTimeMillis() - start;

            log.debug("{} aggregating took {} ms", getClass().getSimpleName(), dur);
            eventsList.clear();
        }
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#submit()
     */
    @Override
    public void submit() {
        engine.submitRule(this);
    }


    /**
     * @param newPeriod
     */
    public void updatePeriod(final long newPeriod) {
        log.debug("updating rule's period {} -> {}", period, newPeriod);
        this.period = newPeriod;
        engine.resubmit(this);
    }


    /**
     * This is called at the end of each period.
     * 
     * @param list
     *            the list of monitoringEvents to aggregate over.
     * @param tStart
     *            the start of the aggregation period (actually the <em>collection</em> period).
     * @param tEnd
     *            the end of the aggregation period.
     * @return the aggregation result, if any.
     */
    protected abstract MonitoringEvent aggregate(final List<MonitoringEvent> list, final long tStart, final long tEnd);


    /**
     * @param e
     */
    protected void collect(final MonitoringEvent e) {
        eventsList.add(e);
    }


    /**
     * @return the period for <code>this</code> rule, in milliseconds.
     */
    protected long period() {
        return period;
    }


    /**
     * @param e
     */
    protected void send(final MonitoringEvent e) {
        engine.send(e);
    }

    /**
     * This is a hack, used to allow rules to run even if there are no events. See {@link MetricsRule}.
     * 
     * @return <code>true</code> if the rule should always run, <code>false</code> otherwise.
     */
     @SuppressWarnings("static-method")
     protected boolean shouldAlwaysRun() {
         return false;
     }


    /**
     * @return a uuid for this rule.
     */
    private static String getId() {
        return UUID.randomUUID().toString();
    }
}
