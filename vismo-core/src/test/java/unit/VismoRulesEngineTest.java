package unit;

import gr.ntua.vision.monitoring.EventSinks;
import gr.ntua.vision.monitoring.EventSourceListener;
import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.RuleProc;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sources.EventSource;

import java.util.ArrayList;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Rudimentary test that verifies that {@link VismoRulesEngine} can trigger rules in series.
 */
@RunWith(JMock.class)
public class VismoRulesEngineTest {
    /***/
    private static class DummyEventSource implements EventSource {
        /***/
        private static final Event                   DUMMY_EVENT = null;
        /***/
        private final ArrayList<EventSourceListener> listeners   = new ArrayList<EventSourceListener>();


        /**
         * Constructor.
         */
        public DummyEventSource() {
            // NOP
        }


        /**
         * @see gr.ntua.vision.monitoring.sources.EventSource#add(gr.ntua.vision.monitoring.EventSourceListener)
         */
        @Override
        public void add(final EventSourceListener listener) {
            listeners.add(listener);
        }


        /***/
        public void pushEvents() {
            for (final EventSourceListener listener : listeners)
                listener.receive(DUMMY_EVENT);
        }
    }
    /***/
    private final Mockery          context     = new JUnit4Mockery();
    /***/
    private final DummyEventSource dummySource = new DummyEventSource();
    /***/
    private final VismoRulesEngine engine      = new VismoRulesEngine(new EventSinks());


    /***/
    @Before
    public void setUp() {
        engine.registerWithSource(dummySource);
    }


    /***/
    @SuppressWarnings("unchecked")
    @Test
    public void whenReceivingAnEventItShouldRunRulesInSeries() {
        final RuleProc<Event> r1 = context.mock(RuleProc.class, "r1");
        final RuleProc<Event> r2 = context.mock(RuleProc.class, "r2");

        engine.addRule(r1);
        engine.addRule(r2);

        context.checking(new Expectations() {
            {
                oneOf(r1).performWith(with(any(Event.class)));
                oneOf(r2).performWith(with(any(Event.class)));
            }
        });

        dummySource.pushEvents();
    }


    /***/
    @Ignore("not implemented yet")
    @Test
    public void whenReceivingAnEventItShouldTriggerPeriodRulesToo() {
        // TODO
    }
}
