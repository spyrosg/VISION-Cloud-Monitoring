package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;


/**
 * This class provides the tools for rules that run periodically.
 */
public abstract class VismoPeriodicRule extends TimerTask implements RuleProc<Event> {
    /***/
    private final VismoRulesEngine engine;
    /***/
    private final ArrayList<Event> events = new ArrayList<Event>();


    /**
     * Constructor.
     * 
     * @param engine
     */
    public VismoPeriodicRule(final VismoRulesEngine engine) {
        this.engine = engine;
    }


    /**
     * @return the period for <code>this</code> rule.
     */
    public abstract long period();


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

            if (result != null)
                send(result);
        } finally {
            events.clear();
        }
    }


    /**
     * This is called at the end of each period.
     * 
     * @param eventList
     *            the list of events to aggregate over.
     * @return the aggregation result.
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
