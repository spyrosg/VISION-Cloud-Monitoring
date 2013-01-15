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
    private final ArrayList<MonitoringEvent> eventsList = new ArrayList<MonitoringEvent>();
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


    /**
     * Run the aggregation method over all collected monitoringEvents. The result of the aggregation if not <code>null</code> will
     * be passed back to the rules engine. Upon completion, the event list will be cleared.
     * 
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        if (eventsList.isEmpty())
            return;

        try {
            send(aggregate(eventsList));
        } finally {
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
}
