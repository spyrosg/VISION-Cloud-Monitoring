package rulespropagation;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.sinks.EventSinks;

import java.io.IOException;
import java.util.ArrayList;


/**
 * @author tmessini
 */
public class RulesPropagationTest2 {

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

        final VismoRulesEngine engine1 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest2.store)));
        engine1.registerToSource(RulesPropagationTest2.source);

        final VismoRulesEngine engine2 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest2.store)));
        engine2.registerToSource(RulesPropagationTest2.source);

        final VismoRulesEngine engine3 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest2.store)));
        engine3.registerToSource(RulesPropagationTest2.source);

        final VismoRulesEngine engine4 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest2.store)));
        engine4.registerToSource(RulesPropagationTest2.source);

        final VismoRulesEngine engine5 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest2.store)));
        engine5.registerToSource(RulesPropagationTest2.source);

        final VismoRulesEngine engine6 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest2.store)));
        engine6.registerToSource(RulesPropagationTest2.source);

        final VismoRulesEngine engine7 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest2.store)));
        engine7.registerToSource(RulesPropagationTest2.source);

        final VismoRulesEngine engine8 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest2.store)));
        engine8.registerToSource(RulesPropagationTest2.source);

        final VismoRulesEngine engine9 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest2.store)));
        engine9.registerToSource(RulesPropagationTest2.source);

        final VismoRulesEngine engine10 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest2.store)));
        engine10.registerToSource(RulesPropagationTest2.source);

        final VismoRulesEngine engine11 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest2.store)));
        engine11.registerToSource(RulesPropagationTest2.source);

        final VismoRulesEngine engine12 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest2.store)));
        engine12.registerToSource(RulesPropagationTest2.source);

        final VismoRulesEngine engine13 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest2.store)));
        engine13.registerToSource(RulesPropagationTest2.source);

        final VismoRulesEngine engine14 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest2.store)));
        engine14.registerToSource(RulesPropagationTest2.source);

        final VismoRulesEngine engine15 = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(RulesPropagationTest2.store)));
        engine15.registerToSource(RulesPropagationTest2.source);

        final RulesPropagationManager rulesManager1 = new RulesPropagationManager(engine1,
                "gr.ntua.vision.monitoring.rules.propagation", 10011);
        final RulesPropagationManager rulesManager2 = new RulesPropagationManager(engine2,
                "gr.ntua.vision.monitoring.rules.propagation", 10012);
        final RulesPropagationManager rulesManager3 = new RulesPropagationManager(engine3,
                "gr.ntua.vision.monitoring.rules.propagation", 10013);
        final RulesPropagationManager rulesManager4 = new RulesPropagationManager(engine4,
                "gr.ntua.vision.monitoring.rules.propagation", 10014);
        final RulesPropagationManager rulesManager5 = new RulesPropagationManager(engine5,
                "gr.ntua.vision.monitoring.rules.propagation", 10015);
        final RulesPropagationManager rulesManager6 = new RulesPropagationManager(engine6,
                "gr.ntua.vision.monitoring.rules.propagation", 10016);
        final RulesPropagationManager rulesManager7 = new RulesPropagationManager(engine7,
                "gr.ntua.vision.monitoring.rules.propagation", 10017);
        final RulesPropagationManager rulesManager8 = new RulesPropagationManager(engine8,
                "gr.ntua.vision.monitoring.rules.propagation", 10018);
        final RulesPropagationManager rulesManager9 = new RulesPropagationManager(engine9,
                "gr.ntua.vision.monitoring.rules.propagation", 10019);
        final RulesPropagationManager rulesManager10 = new RulesPropagationManager(engine10,
                "gr.ntua.vision.monitoring.rules.propagation", 10020);
        final RulesPropagationManager rulesManager11 = new RulesPropagationManager(engine11,
                "gr.ntua.vision.monitoring.rules.propagation", 10021);
        final RulesPropagationManager rulesManager12 = new RulesPropagationManager(engine12,
                "gr.ntua.vision.monitoring.rules.propagation", 10022);
        final RulesPropagationManager rulesManager13 = new RulesPropagationManager(engine13,
                "gr.ntua.vision.monitoring.rules.propagation", 10023);
        final RulesPropagationManager rulesManager14 = new RulesPropagationManager(engine14,
                "gr.ntua.vision.monitoring.rules.propagation", 10024);
        final RulesPropagationManager rulesManager15 = new RulesPropagationManager(engine15,
                "gr.ntua.vision.monitoring.rules.propagation", 10025);

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
