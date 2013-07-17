package integration.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.rules.PeriodicRule;
import gr.ntua.vision.monitoring.rules.Rule;
import gr.ntua.vision.monitoring.rules.RulesStore;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.InMemoryEventSink;
import gr.ntua.vision.monitoring.sources.InMemoryEventSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * This is used to test the whole pipeline of events:
 * <ol>
 * <li>Events enter the rules engine through an event source,</li>
 * <li>Rules are run (get triggered) against those events,</li>
 * <li>Events are transmitted to a sink.</li>
 * </ol>
 */
public class VismoRulesEngineTest {
    /**
     * A rule to increment the field of an event.
     */
    private static class IncRule extends Rule {
        /***/
        private final String key;


        /**
         * Constructor.
         * 
         * @param engine
         * @param key
         */
        public IncRule(final VismoRulesEngine engine, final String key) {
            super(engine);
            this.key = key;
        }


        /**
         * @see gr.ntua.vision.monitoring.rules.Rule#id()
         */
        @Override
        public String id() {
            return toString();
        }


        /**
         * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
         */
        @Override
        public void performWith(final MonitoringEvent e) {
            final int val = (Integer) e.get(key);

            send(newEvent(key, val + 1));
        }


        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "#<IncRule: " + key + ">";
        }
    }


    /**
     * A rule to sum integers.
     */
    private static class IntSumRule extends PeriodicRule {
        /***/
        private final String key;


        /**
         * Constructor.
         * 
         * @param engine
         * @param period
         * @param key
         */
        public IntSumRule(final VismoRulesEngine engine, final long period, final String key) {
            super(engine, period);
            this.key = key;
        }


        /**
         * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
         */
        @Override
        public void performWith(final MonitoringEvent e) {
            if (matches(e))
                collect(e);
        }


        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "#<IntSumRule: " + key + ">";
        }


        /**
         * @see gr.ntua.vision.monitoring.rules.PeriodicRule#aggregate(java.util.List, long, long)
         */
        @Override
        protected MonitoringEvent aggregate(final List<MonitoringEvent> list, final long tStart, final long tEnd) {
            final ArrayList<Integer> intList = new ArrayList<Integer>(list.size());
            extract(intList, list);

            return newEvent(key, sum(intList));
        }


        /**
         * @param intList
         * @param eventList
         */
        private void extract(final ArrayList<Integer> intList, final List<MonitoringEvent> eventList) {
            for (final MonitoringEvent e : eventList)
                intList.add((Integer) e.get(key));
        }


        /**
         * @param e
         * @return <code>true</code> if the event contains the specified key.
         */
        private boolean matches(final MonitoringEvent e) {
            return e.get(key) != null;
        }


        /**
         * @param intList
         * @return foldl (+) intList 0
         */
        private static int sum(final ArrayList<Integer> intList) {
            int sum = 0;

            for (final int i : intList)
                sum += i;

            return sum;
        }
    }

    /***/
    private static final VismoEventFactory   factory     = new VismoEventFactory();
    /** the object under test. */
    private VismoRulesEngine                 engine;
    /** this is where the events should end up. */
    private final ArrayList<MonitoringEvent> eventsStore = new ArrayList<MonitoringEvent>();
    /***/
    private final RulesStore                 rulesStore  = new RulesStore();
    /***/
    private final InMemoryEventSource        source      = new InMemoryEventSource();


    /***/
    @Test
    public void canRemoveRule() {
        final IncRule r1 = new IncRule(engine, "foo");
        final IncRule r2 = new IncRule(engine, "foo");
        final IntSumRule r3 = new IntSumRule(engine, 10, "foo");

        r1.submit();
        r2.submit();
        r3.submit();

        assertEquals(2, rulesStore.size());

        engine.removeRule(r1);
        assertFalse(rulesStore.contains(r1));
        assertFalse(rulesStore.contains(r2));
        assertTrue(rulesStore.contains(r3));
    }


    /***/
    @Test
    public void canSubmitRule() {
        final IncRule rule = new IncRule(engine, "foo");

        rule.submit();
        assertTrue(rulesStore.contains(rule));
    }


    /**
     * @throws InterruptedException
     */
    @Test
    public void itShouldEvaluateAnAsynchronousRule() throws InterruptedException {
        final String KEY = "foo";
        final int VAL1 = 2;
        final int VAL2 = 3;
        final long RULE_PERIOD = 100;
        final long TIMEOUT = 150;

        new IntSumRule(engine, RULE_PERIOD, KEY).submit();
        source.triggerRuleEvaluationWith(newEvent(KEY, VAL1));
        source.triggerRuleEvaluationWith(newEvent(KEY, VAL2));

        assertEquals(0, eventsStore.size());
        waitTimerToTriggerRuleAggregation(TIMEOUT);
        assertEquals(1, eventsStore.size());

        final MonitoringEvent d = lastEvent();

        assertEquals(VAL1 + VAL2, d.get(KEY));
    }


    /***/
    @Test
    public void itShouldEvaluateASynchonousRule() {
        final String KEY = "foo";
        final int VAL = 0;

        new IncRule(engine, KEY).submit();
        source.triggerRuleEvaluationWith(newEvent(KEY, VAL));

        assertEquals(1, eventsStore.size());

        final MonitoringEvent d = lastEvent();

        assertEquals(VAL + 1, d.get(KEY));
    }


    /**
     * 
     */
    @Before
    public void setUp() {
        engine = new VismoRulesEngine(rulesStore);
        engine.registerToSource(source);
        engine.appendSink(new InMemoryEventSink(eventsStore));
    }


    /**
     * @return the last event from the store.
     */
    private MonitoringEvent lastEvent() {
        return eventsStore.get(eventsStore.size() - 1);
    }


    /**
     * @param key
     * @param val
     * @return a new {@link MonitoringEvent}.
     */
    static MonitoringEvent newEvent(final String key, final Object val) {
        final HashMap<String, Object> map = new HashMap<String, Object>();

        map.put(key, val);
        map.put("timestamp", System.currentTimeMillis());
        map.put("originating-service", VismoRulesEngineTest.class.getSimpleName());
        map.put("originating-machine", "localhost");

        return factory.createEvent(map);
    }


    /**
     * @param n
     * @throws InterruptedException
     */
    private static void waitTimerToTriggerRuleAggregation(final long n) throws InterruptedException {
        Thread.sleep(n);
    }
}
