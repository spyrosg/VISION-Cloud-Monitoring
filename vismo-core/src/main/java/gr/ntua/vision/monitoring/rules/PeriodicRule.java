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


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((engine == null) ? 0 : engine.hashCode());
        result = prime * result + ((events == null) ? 0 : events.hashCode());
        result = prime * result + (int) (period ^ (period >>> 32));
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PeriodicRule other = (PeriodicRule) obj;
        if (engine == null) {
            if (other.engine != null)
                return false;
        } else if (!engine.equals(other.engine))
            return false;
        if (events == null) {
            if (other.events != null)
                return false;
        } else if (!events.equals(other.events))
            return false;
        if (period != other.period)
            return false;
        return true;
    }
}
