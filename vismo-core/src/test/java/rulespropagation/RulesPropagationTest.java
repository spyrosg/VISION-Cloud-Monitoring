package rulespropagation;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sinks.EventSinks;
import gr.ntua.vision.monitoring.sources.EventSource;
import gr.ntua.vision.monitoring.sources.EventSourceListener;

import java.io.IOException;
import java.util.ArrayList;


/**
 * @author tmessini
 */
public class RulesPropagationTest {

    /***/
    private final static InMemoryEventSource source = new InMemoryEventSource();
    /** this is where the events should end up. */
    private final static ArrayList<Event>    store  = new ArrayList<Event>();


    /**
     * testing the functionality.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {

        final VismoRulesEngine engine1 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest.store)));
        engine1.registerToSource(RulesPropagationTest.source);

        final VismoRulesEngine engine2 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest.store)));
        engine2.registerToSource(RulesPropagationTest.source);

        final VismoRulesEngine engine3 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest.store)));
        engine3.registerToSource(RulesPropagationTest.source);

        final VismoRulesEngine engine4 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest.store)));
        engine4.registerToSource(RulesPropagationTest.source);

        final VismoRulesEngine engine5 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest.store)));
        engine5.registerToSource(RulesPropagationTest.source);

        final VismoRulesEngine engine6 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest.store)));
        engine6.registerToSource(RulesPropagationTest.source);

        final VismoRulesEngine engine7 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest.store)));
        engine7.registerToSource(RulesPropagationTest.source);

        final VismoRulesEngine engine8 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest.store)));
        engine8.registerToSource(RulesPropagationTest.source);

        final VismoRulesEngine engine9 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest.store)));
        engine9.registerToSource(RulesPropagationTest.source);

        final VismoRulesEngine engine10 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest.store)));
        engine10.registerToSource(RulesPropagationTest.source);

        final VismoRulesEngine engine11 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest.store)));
        engine11.registerToSource(RulesPropagationTest.source);

        final VismoRulesEngine engine12 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest.store)));
        engine12.registerToSource(RulesPropagationTest.source);

        final VismoRulesEngine engine13 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest.store)));
        engine13.registerToSource(RulesPropagationTest.source);

        final VismoRulesEngine engine14 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest.store)));
        engine14.registerToSource(RulesPropagationTest.source);

        final VismoRulesEngine engine15 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest.store)));
        engine15.registerToSource(RulesPropagationTest.source);

        final RulesPropagationManager rulesManager1 = new RulesPropagationManager(engine1,
                "gr.ntua.vision.monitoring.rules.propagation", 9996);
        final RulesPropagationManager rulesManager2 = new RulesPropagationManager(engine2,
                "gr.ntua.vision.monitoring.rules.propagation", 9997);
        final RulesPropagationManager rulesManager3 = new RulesPropagationManager(engine3,
                "gr.ntua.vision.monitoring.rules.propagation", 9998);
        final RulesPropagationManager rulesManager4 = new RulesPropagationManager(engine4,
                "gr.ntua.vision.monitoring.rules.propagation", 9999);
        final RulesPropagationManager rulesManager5 = new RulesPropagationManager(engine5,
                "gr.ntua.vision.monitoring.rules.propagation", 10000);
        final RulesPropagationManager rulesManager6 = new RulesPropagationManager(engine6,
                "gr.ntua.vision.monitoring.rules.propagation", 10001);
        final RulesPropagationManager rulesManager7 = new RulesPropagationManager(engine7,
                "gr.ntua.vision.monitoring.rules.propagation", 10002);
        final RulesPropagationManager rulesManager8 = new RulesPropagationManager(engine8,
                "gr.ntua.vision.monitoring.rules.propagation", 10003);
        final RulesPropagationManager rulesManager9 = new RulesPropagationManager(engine9,
                "gr.ntua.vision.monitoring.rules.propagation", 10004);
        final RulesPropagationManager rulesManager10 = new RulesPropagationManager(engine10,
                "gr.ntua.vision.monitoring.rules.propagation", 10005);
        final RulesPropagationManager rulesManager11 = new RulesPropagationManager(engine11,
                "gr.ntua.vision.monitoring.rules.propagation", 10006);
        final RulesPropagationManager rulesManager12 = new RulesPropagationManager(engine12,
                "gr.ntua.vision.monitoring.rules.propagation", 10007);
        final RulesPropagationManager rulesManager13 = new RulesPropagationManager(engine13,
                "gr.ntua.vision.monitoring.rules.propagation", 10008);
        final RulesPropagationManager rulesManager14 = new RulesPropagationManager(engine14,
                "gr.ntua.vision.monitoring.rules.propagation", 10009);
        final RulesPropagationManager rulesManager15 = new RulesPropagationManager(engine15,
                "gr.ntua.vision.monitoring.rules.propagation", 10010);

        rulesManager1.start();
        rulesManager2.start();
        rulesManager3.start();
        rulesManager4.start();
        rulesManager5.start();
        rulesManager6.start();
        rulesManager7.start();
        rulesManager8.start();
        rulesManager9.start();
        rulesManager10.start();
        rulesManager11.start();
        rulesManager12.start();
        rulesManager13.start();
        rulesManager14.start();
        rulesManager15.start();

    }

}


/**
 * 
 */
class InMemoryEventSink implements EventSink {
    /***/
    private final ArrayList<Event> eventStore;


    /**
     * Constructor.
     * 
     * @param eventStore
     */
    public InMemoryEventSink(final ArrayList<Event> eventStore) {
        this.eventStore = eventStore;
    }


    /**
     * @see gr.ntua.vision.monitoring.sinks.EventSink#send(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void send(final Event e) {
        eventStore.add(e);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<InMemoryEventSink: " + eventStore + ">";
    }
}


/**
 * @author tmessini
 */
class InMemoryEventSource implements EventSource {
    /***/
    private final ArrayList<EventSourceListener> listeners = new ArrayList<EventSourceListener>();


    /**
     * Constructor.
     */
    public InMemoryEventSource() {
        // NOP
    }


    /**
     * @see gr.ntua.vision.monitoring.sources.EventSource#add(gr.ntua.vision.monitoring.sources.EventSourceListener)
     */
    @Override
    public void add(final EventSourceListener listener) {
        listeners.add(listener);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<InMemoryEventSource>";
    }


    /**
     * @param e
     */
    public void triggerRuleEvaluationWith(final Event e) {
        for (final EventSourceListener listener : listeners)
            listener.receive(e);
    }
}
