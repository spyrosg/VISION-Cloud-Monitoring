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
        waitForAggregation(PERIOD);
        assertProperAggregationResult();
        assertLastEventIsFromStorletEngineRule();

        final MonitoringEvent e = eventsList.get(eventsList.size() - 1);
        @SuppressWarnings("unchecked")
        final List<HashMap<String, Object>> groups = (List<HashMap<String, Object>>) e.get("groups");

        assertEquals(GROUPS_OF_EVENTS, groups.size());
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
     * @param n
     * @throws InterruptedException
     */
    private static void waitForAggregation(final long n) throws InterruptedException {
        Thread.sleep(n);
    }
}
