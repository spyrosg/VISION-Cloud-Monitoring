package unit.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.rules.AccountingRule;
import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.VismoAggregationResult;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.InMemoryEventSink;
import helpers.InMemoryEventDispatcher;
import integration.tests.FakeObjectService;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;


/**
 * This is used to test that {@link VismoAggregationResult}'s contain the required fields.
 */
public class VismoAggregationResultTest {
    /***/
    private static final long                RULE_PERIOD = 500;
    /***/
    private VismoRulesEngine                 engine;
    /***/
    private final ArrayList<MonitoringEvent> eventList   = new ArrayList<MonitoringEvent>();
    /***/
    private FakeObjectService                service;
    /***/
    private final InMemoryEventSink          sink        = new InMemoryEventSink(eventList);


    /**
     * @throws InterruptedException
     */
    @Test
    public void aggregationResultShouldInludeRequiredFields() throws InterruptedException {
        new AccountingRule(engine, RULE_PERIOD).submit();

        for (int i = 0; i < 3; ++i)
            service.putEvent("ntua", "bill", "foo", "bar").send();

        Thread.sleep((long) (1.5 * RULE_PERIOD));
        assertEquals(1, eventList.size());

        final VismoAggregationResult res = (VismoAggregationResult) eventList.get(0);

        assertTrue(res.timestamp() > 0);
        assertTrue(res.tStart() > 0);
        assertTrue(res.tEnd() > 0);
    }


    /***/
    @Before
    public void setUp() {
        engine = new VismoRulesEngine();
        engine.appendSink(sink);
        service = new FakeObjectService(new InMemoryEventDispatcher(engine, "fake-obs"));
    }


    /**
     * @throws InterruptedException
     */
    @Test
    public void shouldPassEventsFromServiceToSink() throws InterruptedException {
        new PassThroughRule(engine).submit();

        for (int i = 0; i < 3; ++i)
            service.putEvent("ntua", "bill", "foo", "bar").send();

        Thread.sleep(100); // wait for events to be received
        assertEquals(3, eventList.size());
    }
}
