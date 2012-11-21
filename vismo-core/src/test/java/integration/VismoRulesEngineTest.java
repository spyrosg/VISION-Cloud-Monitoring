package integration;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.EventSinks;
import gr.ntua.vision.monitoring.EventSourceListener;
import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.VismoRule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sources.EventSource;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

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
        public Object get(final String key) {
            if (this.key.equals(key))
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
    private static class IncRule extends VismoRule {
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
         * @see gr.ntua.vision.monitoring.sources.EventSource#add(gr.ntua.vision.monitoring.EventSourceListener)
         */
        @Override
        public void add(final EventSourceListener listener) {
            listeners.add(listener);
        }


        /**
         * @param e
         */
        public void triggerRuleEvaluationWith(final Event e) {
            for (final EventSourceListener listener : listeners)
                listener.receive(e);
        }
    }

    /** the object under test. */
    private VismoRulesEngine          engine;
    /**  */
    private InMemoryEventSink         sink;
    /***/
    private final InMemoryEventSource source = new InMemoryEventSource();
    /** this is where the events should end up. */
    private final ArrayList<Event>    store  = new ArrayList<Event>();


    /***/
    @Test
    public void itShouldEvaluateASynchonousRule() {
        final String KEY = "foo";
        final int VAL = 0;

        engine.addRule(new IncRule(engine, KEY));
        source.triggerRuleEvaluationWith(new DummyEvent(KEY, VAL));

        assertEquals(1, store.size());

        final Event d = store.get(0);

        assertEquals(VAL + 1, d.get(KEY));
    }


    /**
     * 
     */
    @Before
    public void setUp() {
        sink = new InMemoryEventSink(store);
        engine = new VismoRulesEngine(new EventSinks(sink));
        source.add(engine);
    }
}
