package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.ArrayList;
import java.util.TimerTask;


/**
 *
 */
public abstract class PeriodicRule extends TimerTask implements AggregationRule {
    /** the list of events matched to the rule in the last period. */
    private final ArrayList<Event> matchedEvents = new ArrayList<Event>();
    /** the period for this rule. */
    private final long             period;


    /**
     * Constructor.
     * 
     * @param period
     *            the period for this rule.
     */
    public PeriodicRule(final long period) {
        this.period = period;
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationRule#collect(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void collect(final Event e) {
        matchedEvents.add(e);
    }


    /**
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        try {
            aggregate(matchedEvents);
        } catch (final Throwable x) {
            // TODO
        } finally {
            matchedEvents.clear();
        }
    }
}
