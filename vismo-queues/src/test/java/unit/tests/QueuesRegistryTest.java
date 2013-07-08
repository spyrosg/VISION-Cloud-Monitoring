package unit.tests;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.queues.QueuesRegistry;

import java.util.List;

import junit.framework.TestCase;


/**
 * 
 */
public class QueuesRegistryTest extends TestCase {
    /***/
    private static final int      NO_EVENTS = 100;
    /***/
    private InMemoryEventRegistry eventGenerator;
    /***/
    private QueuesRegistry        registry;


    /**
     * @throws Exception
     */
    public void testShouldRegisterAndReceiveEvents() throws Exception {
        registry.register("my-queue", "*");
        eventGenerator.pushEvents(NO_EVENTS);

        final List<MonitoringEvent> events = registry.getEvents("my-queue");

        assertEquals(NO_EVENTS, events.size());
    }


    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        eventGenerator = new InMemoryEventRegistry();
        registry = new QueuesRegistry(eventGenerator);
    }


    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        if (registry != null)
            registry.halt();

        super.tearDown();
    }
}
