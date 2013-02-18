package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sources.EventSource;
import gr.ntua.vision.monitoring.sources.EventSourceListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to evaluate the rules. Rules are triggered from events, which are received from one or more {@link EventSource}s
 * and the evaluation result (if any) is passed to one or more {@link EventSink}s. Rules are triggered in the order they were
 * added in the engine, but might produce results in an unspecified order (which basically boils down to that some rules will be
 * triggered by a timer thread).
 */
public class VismoRulesEngine implements EventSourceListener {
    /***/
    private static final Logger        log   = LoggerFactory.getLogger(VismoRulesEngine.class);
    /***/
    private final ArrayList<EventSink> sinks = new ArrayList<EventSink>();
    /***/
    private final RulesStore           store;
    /***/
    private final Timer                timer = new Timer(true);


    /**
     * Constructor.
     * 
     * @param store
     */
    public VismoRulesEngine(final RulesStore store) {
        this.store = store;
    }


    /**
     * Append given sink to the engine.
     * 
     * @param sink
     *            the sink to add.
     */
    public void appendSink(final EventSink sink) {
        log.debug("adding sink: {}", sink);
        sinks.add(sink);
    }


    /**
     * Append the collection of lists to the engine.
     * 
     * @param coll
     *            a collection of sinks.
     */
    public void appendSinks(final List< ? extends EventSink> coll) {
        log.debug("adding sinks: {}", coll);
        sinks.addAll(coll);
    }


    /**
     * @return number of rules
     */
    public int getRulesTotalNumber() {
        return store.size();

    }


    /**
     * Turn off the engine. No more rules will be run.
     */
    public void halt() {
        timer.cancel();
        store.clear();
    }


    /**
     * @see gr.ntua.vision.monitoring.sources.EventSourceListener#receive(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void receive(final MonitoringEvent e) {
        evaluateRulesAgainst(e);
    }


    /**
     * Register with source.
     * 
     * @param source
     *            the event source.
     */
    public void registerToSource(final EventSource source) {
        log.debug("registering with {}", source);
        source.add(this);
    }


    /**
     * Remove a rule from the rule engine.
     * 
     * @param rule
     *            the rule.
     */
    public void removeRule(final RuleProc<MonitoringEvent> rule) {
        // FIXME: periodic rules aren't removed from the timer
        log.debug("removing {}", rule);
        store.remove(rule);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<VismoRulesEngine>";
    }


    /**
     * @param e
     */
    void send(final MonitoringEvent e) {
        for (final EventSink sink : sinks)
            sink.send(e);
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
    private void add(final RuleProc<MonitoringEvent> r) {
        log.debug("submitting {}", r);
        store.add(r);
    }


    /**
     * Run the event through all rules.
     * 
     * @param e
     *            the event.
     */
    private void evaluateRulesAgainst(final MonitoringEvent e) {
        store.forEach(new RuleOperation() {
            @Override
            public void run(final RuleProc<MonitoringEvent> r) {
                r.performWith(e);
            }
        });
    }


    /**
     * @param rule
     */
    private void schedule(final PeriodicRule rule) {
        timer.schedule(rule, 0, rule.period());
    }
}
