package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.EventSinks;
import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.sources.EventSource;
import gr.ntua.vision.monitoring.sources.EventSourceListener;

import java.util.ArrayList;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to evaluate the rules. Rules are triggered from events, which are received from one or more {@link EventSource}s
 * and the evaluation result (if any) is passed to one or more {@link EventSinks}. Rules are triggered in the order they were
 * added in the engine, but might produce results in an unspecified order (which basically boils down to that some rules will be
 * triggered by a timer thread).
 */
public class VismoRulesEngine implements EventSourceListener {
    /***/
    private static final Logger              log   = LoggerFactory.getLogger(VismoRulesEngine.class);
    /***/
    private final ArrayList<RuleProc<Event>> rules = new ArrayList<RuleProc<Event>>();
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
        log.debug("using {}", sinks);
        this.sinks = sinks;
    }


    /**
     * @see gr.ntua.vision.monitoring.sources.EventSourceListener#receive(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void receive(final Event e) {
        evaluateRulesAgainst(e);
    }


    /**
     * Register with source.
     * 
     * @param source
     *            the event source.
     */
    public void registerWithSource(final EventSource source) {
        log.debug("registering with {}", source);
        source.add(this);
    }


    /**
     * Remove a rule from the rule engine.
     * 
     * @param rule
     *            the rule.
     */
    public void removeRule(final RuleProc<Event> rule) {
        log.debug("removing {}", rule);
        rules.remove(rule);
    }


    /**
     * @param e
     */
    public void send(final Event e) {
        sinks.push(e);
    }


    /**
     * Turn off the engine. No more rules will be run.
     */
    public void shutDown() {
        timer.cancel();
    }


    /**
     * @param rule
     */
    void submitRule(final PeriodicRule rule) {
        add(rule);
        schedule(rule);
    }


    /**
     * Add a rule to run to the rule engine. The rule will run the next time there's a new event.
     * 
     * @param rule
     *            the rule.
     */
    void submitRule(final Rule rule) {
        add(rule);
    }


    /**
     * @param r
     */
    private void add(final RuleProc<Event> r) {
        log.debug("submitting {}", r);
        rules.add(r);
    }


    /**
     * Run the event through all rules.
     * 
     * @param e
     *            the event.
     */
    private void evaluateRulesAgainst(final Event e) {
        for (final RuleProc<Event> r : rules)
            r.performWith(e);
    }


    /**
     * @param rule
     */
    private void schedule(final PeriodicRule rule) {
        timer.schedule(rule, 0, rule.period());
    }
}
