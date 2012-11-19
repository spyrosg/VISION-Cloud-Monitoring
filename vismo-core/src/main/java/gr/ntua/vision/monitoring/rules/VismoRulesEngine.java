package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.EventSinks;
import gr.ntua.vision.monitoring.EventSourceListener;
import gr.ntua.vision.monitoring.events.Event;

import java.util.ArrayList;
import java.util.Timer;


/**
 *
 */
public class VismoRulesEngine implements EventSourceListener {
    /***/
    private final ArrayList<AggregationRule> rules = new ArrayList<AggregationRule>();
    /***/
    private final EventSinks                 sinks;
    /***/
    private final Timer                      timer = new Timer();


    /**
     * Constructor.
     * 
     * @param sinks
     */
    public VismoRulesEngine(final EventSinks sinks) {
        this.sinks = sinks;
    }


    /**
     * @see gr.ntua.vision.monitoring.EventSourceListener#receive(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void receive(final Event e) {
        // TODO Auto-generated method stub
    }


    /**
     * @param rule
     */
    public void removeRule(final AggregationRule rule) {
        rules.remove(rule);
    }


    /**
     * @param rule
     */
    public void submitRule(final AggregationRule rule) {
        rules.add(rule);
    }
}
