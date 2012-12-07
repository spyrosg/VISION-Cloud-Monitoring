package integration;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.PeriodicRule;
import gr.ntua.vision.monitoring.rules.Rule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sinks.EventSinks;
import gr.ntua.vision.monitoring.sources.EventSource;
import gr.ntua.vision.monitoring.sources.EventSourceListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
     * A minimal event implementation, with one just key/value pair.
     */
    private static class DummyEvent implements Event {
        /***/
        private final String key;
        /***/
        private final Object val;


        /**
         * Constructor.
         * 
         * @param key
         * @param val
         */
        public DummyEvent(final String key, final Object val) {
            this.key = key;
            this.val = val;
        }


        /**
         * @see gr.ntua.vision.monitoring.events.Event#get(java.lang.String)
         */
        @Override
        public Object get(final String k) {
            if (this.key.equals(k))
                return val;

            return null;
        }


        /**
         * @see gr.ntua.vision.monitoring.events.Event#originatingIP()
         */
        @Override
        public InetAddress originatingIP() throws UnknownHostException {
            return null;
        }


        /**
         * @see gr.ntua.vision.monitoring.events.Event#originatingService()
         */
        @Override
        public String originatingService() {
            return null;
        }


        /**
         * @see gr.ntua.vision.monitoring.events.Event#timestamp()
         */
        @Override
        public long timestamp() {
            return 0;
        }


        /**
         * @see gr.ntua.vision.monitoring.events.Event#topic()
         */
        @Override
        public String topic() {
            return null;
        }
    }


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
         * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
         */
        @Override
        public void performWith(final Event e) {
            final int val = (Integer) e.get(key);

            send(new DummyEvent(key, val + 1));
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
     * 
     */
    private static class InMemoryEventSink implements EventSink {
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
     * 
     */
    private static class InMemoryEventSource implements EventSource {
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
        public void performWith(final Event e) {
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
         * @see gr.ntua.vision.monitoring.rules.PeriodicRule#aggregate(java.util.List)
         */
        @Override
        protected Event aggregate(final List<Event> eventList) {
            if (eventList.size() == 0)
                return null;

            final ArrayList<Integer> intList = new ArrayList<Integer>(eventList.size());
            extract(intList, eventList);

            return new DummyEvent(key, sum(intList));
        }


        /**
         * @param intList
         * @param eventList
         */
        private void extract(final ArrayList<Integer> intList, final List<Event> eventList) {
            for (final Event e : eventList)
                intList.add((Integer) e.get(key));
        }


        /**
         * @param e
         * @return <code>true</code> if the event contains the specified key.
         */
        private boolean matches(final Event e) {
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

    /** the object under test. */
    private VismoRulesEngine          engine;
    /***/
    private final InMemoryEventSource source = new InMemoryEventSource();
    /** this is where the events should end up. */
    private final ArrayList<Event>    store  = new ArrayList<Event>();


    /***/
    @Test
    public void itShouldEvaluateAnAsynchronousRule() {
        final String KEY = "foo";
        final int VAL1 = 2;
        final int VAL2 = 3;
        final long RULE_PERIOD = 100;
        final long TIMEOUT = 150;

        new IntSumRule(engine, RULE_PERIOD, KEY).submitTo(engine);
        source.triggerRuleEvaluationWith(new DummyEvent(KEY, VAL1));
        source.triggerRuleEvaluationWith(new DummyEvent(KEY, VAL2));

        assertEquals(0, store.size());
        waitTimerToTriggerRuleAggregation(TIMEOUT);
        assertEquals(1, store.size());

        final Event d = lastEvent();

        assertEquals(VAL1 + VAL2, d.get(KEY));
    }


    /***/
    @Test
    public void itShouldEvaluateASynchonousRule() {
        final String KEY = "foo";
        final int VAL = 0;

        new IncRule(engine, KEY).submitTo(engine);
        source.triggerRuleEvaluationWith(new DummyEvent(KEY, VAL));

        assertEquals(1, store.size());

        final Event d = lastEvent();

        assertEquals(VAL + 1, d.get(KEY));
    }


    /**
     * 
     */
    @Before
    public void setUp() {
        engine = new VismoRulesEngine(new EventSinks(new InMemoryEventSink(store)));
        engine.registerToSource(source);
    }


    /**
     * @return the last event from the store.
     */
    private Event lastEvent() {
        return store.get(store.size() - 1);
    }


    /**
     * @param n
     */
    private static void waitTimerToTriggerRuleAggregation(final long n) {
        try {
            Thread.sleep(n);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }
}