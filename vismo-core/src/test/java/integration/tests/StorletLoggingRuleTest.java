package integration.tests;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.RulesStore;
import gr.ntua.vision.monitoring.rules.StorletLoggingRule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.InMemoryEventSink;
import helpers.InMemoryEventDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class StorletLoggingRuleTest {
    /***/
    private static final String              EXPECTED_TOPIC    = "_SLLOG";
    /***/
    private static final int                 GROUPS_OF_EVENTS  = 4;
    /***/
    private static final int                 NO_EVENTS_TO_SEND = 100;
    /** the rule's period in seconds. */
    private static final long                PERIOD            = 500;
    /***/
    private final VismoRulesEngine           engine            = new VismoRulesEngine(new RulesStore());
    /***/
    private final ArrayList<MonitoringEvent> eventsList        = new ArrayList<MonitoringEvent>();
    /***/
    private final InMemoryEventSink          sink              = new InMemoryEventSink(eventsList);
    /***/
    private final FakeStorletEngine          storletEngine     = new FakeStorletEngine(new InMemoryEventDispatcher(engine));


    /***/
    @Before
    public void setUp() {
        engine.appendSink(sink);
    }


    /**
     * @throws InterruptedException
     */
    @Test
    public void storletLoggingRuleAggregatesProperly() throws InterruptedException {
        new PassThroughRule(engine).submit();
        new StorletLoggingRule(engine, PERIOD).submit();

        sendEvents(NO_EVENTS_TO_SEND);
        waitForAggregation();
        assertProperAggregationResult();
        assertLastEventIsFromStorletEngineRule();

        final MonitoringEvent e = eventsList.get(eventsList.size() - 1);
        @SuppressWarnings("unchecked")
        final List<HashMap<String, Object>> groups = (List<HashMap<String, Object>>) e.get("groups");

        assertEquals(GROUPS_OF_EVENTS, groups.size());
        assertProperOrderOfGroupMessages(groups);
    }


    /***/
    private void assertLastEventIsFromStorletEngineRule() {
        assertEquals(eventsList.get(eventsList.size() - 1).topic(), EXPECTED_TOPIC);
    }


    /***/
    private void assertProperAggregationResult() {
        assertEquals(NO_EVENTS_TO_SEND + 1, eventsList.size());
    }


    /**
     * @param noEventsToSend
     */
    private void sendEvents(final int noEventsToSend) {
        for (int i = 0; i < GROUPS_OF_EVENTS; ++i)
            for (int j = 0; j < noEventsToSend / GROUPS_OF_EVENTS; ++j)
                storletEngine.sendEvent("id" + i, "activ-" + i, "foo[ " + i * j + " ]bar");
    }


    /**
     * @param groups
     */
    private static void assertProperOrderOfGroupMessages(final List<HashMap<String, Object>> groups) {
        for (final HashMap<String, Object> group : groups) {
            @SuppressWarnings("unchecked")
            final ArrayList<HashMap<String, Object>> messages = (ArrayList<HashMap<String, Object>>) group.get("messages");

            System.err.println("messages: " + messages);
            for (int i = 0; i < messages.size() - 1; ++i) {
                final long currTs = (Long) messages.get(i).get("timestamp");
                final long nextTs = (Long) messages.get(i + 1).get("timestamp");

                if (currTs > nextTs)
                    throw new AssertionError("storlet group messages should be sorted by time");
            }
        }
    }


    /**
     * @throws InterruptedException
     */
    private static void waitForAggregation() throws InterruptedException {
        Thread.sleep((long) (PERIOD * 1.1));
    }
}
