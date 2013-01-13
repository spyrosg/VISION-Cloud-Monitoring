package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;


/**
 * This class provides for the low level responsibilities for a rule that runs periodically, over a list of monitoringEvents.
 */
public abstract class PeriodicRule extends TimerTask implements RuleProc<MonitoringEvent> {
    /***/
    private final VismoRulesEngine           engine;
    /***/
    private final ArrayList<MonitoringEvent> monitoringEvents = new ArrayList<MonitoringEvent>();
    /** the rule's period, in milliseconds. */
    private final long                       period;


    /**
     * Constructor.
     * 
     * @param engine
     * @param period
     *            the rule's period, in milliseconds.
     */
    public PeriodicRule(final VismoRulesEngine engine, final long period) {
        this.engine = engine;
        this.period = period;
    }


    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PeriodicRule other = (PeriodicRule) obj;
        if (engine == null) {
            if (other.engine != null)
                return false;
        } else if (!engine.equals(other.engine))
            return false;
        if (monitoringEvents == null) {
            if (other.monitoringEvents != null)
                return false;
        } else if (!monitoringEvents.equals(other.monitoringEvents))
            return false;
        if (period != other.period)
            return false;
        return true;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((engine == null) ? 0 : engine.hashCode());
        result = prime * result + ((monitoringEvents == null) ? 0 : monitoringEvents.hashCode());
        result = prime * result + (int) (period ^ (period >>> 32));
        return result;
    }


    /**
     * @return the period for <code>this</code> rule, in milliseconds.
     */
    public long period() {
        return period;
    }


    /**
     * Run the aggregation method over all collected monitoringEvents. The result of the aggregation if not <code>null</code> will
     * be passed back to the rules engine. Upon completion, the event list will be cleared.
     * 
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        try {
            final MonitoringEvent result = aggregate(monitoringEvents);

            // TODO: move this desision (send or drop) to MonitoringEvent.
            if (result != null)
                send(result);
        } finally {
            monitoringEvents.clear();
        }
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#submit()
     */
    @Override
    public void submit() {
        this.engine.submitRule(this);
    }


    /**
     * This is called at the end of each period.
     * 
     * @param eventList
     *            the list of monitoringEvents to aggregate over.
     * @return the aggregation result, if any.
     */
    protected abstract MonitoringEvent aggregate(final List<MonitoringEvent> eventList);


    /**
     * @param e
     */
    protected void collect(final MonitoringEvent e) {
        monitoringEvents.add(e);
    }


    /**
     * @param e
     */
    protected void send(final MonitoringEvent e) {
        engine.send(e);
    }
}
