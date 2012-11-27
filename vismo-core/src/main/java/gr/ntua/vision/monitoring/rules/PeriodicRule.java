package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;


/**
 * This class provides for the low level responsibilities for a rule that runs periodically, over a list of events.
 */
public abstract class PeriodicRule extends TimerTask implements RuleProc<Event> {
    /***/
    private final VismoRulesEngine engine;
    /***/
    private final ArrayList<Event> events = new ArrayList<Event>();
    /** the rule's period, in milliseconds. */
    private final long             period;


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
     * @return the period for <code>this</code> rule, in milliseconds.
     */
    public long period() {
        return period;
    }


    /**
     * Run the aggregation method over all collected events. The result of the aggregation if not <code>null</code> will be passed
     * back to the rules engine. Upon completion, the event list will be cleared.
     * 
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        try {
            final Event result = aggregate(events);

            // TODO: move this desision (send or drop) to Event.
            if (result != null)
                send(result);
        } finally {
            events.clear();
        }
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#submitTo(gr.ntua.vision.monitoring.rules.VismoRulesEngine)
     */
    @Override
    public void submitTo(final VismoRulesEngine engine) {
        engine.submitRule(this);
    }


    /**
     * This is called at the end of each period.
     * 
     * @param eventList
     *            the list of events to aggregate over.
     * @return the aggregation result, if any.
     */
    protected abstract Event aggregate(final List<Event> eventList);


    /**
     * @param e
     */
    protected void collect(final Event e) {
        events.add(e);
    }


    /**
     * @param e
     */
    protected void send(final Event e) {
        engine.send(e);
    }
}
