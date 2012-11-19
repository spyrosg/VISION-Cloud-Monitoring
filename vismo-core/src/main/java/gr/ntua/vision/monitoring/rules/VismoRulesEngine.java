package gr.ntua.vision.monitoring.rules;

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
    private final Timer                      timer = new Timer();


    /**
     * @see gr.ntua.vision.monitoring.EventSourceListener#receive(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void receive(final Event e) {
        for (final AggregationRule rule : rules)
            if (rule.matches(e))
                rule.collect(e);
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
