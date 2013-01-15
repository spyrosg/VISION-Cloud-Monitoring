package unit.tests;

import gr.ntua.vision.monitoring.dispatch.EventBuilder;
import gr.ntua.vision.monitoring.dispatch.EventDispatcher;
import integration.tests.FakeObjectService;

import org.junit.Before;
import org.junit.Test;


/**
 * 
 */

// {"transaction-throughput":12821.185185185184,"status":"SUCCESS","object":"bar-object","type":"read","content-size":692344,"originating-service":"fake-obs","id":"ed6f7609-5ce4-433d-bf53-9a79ddad5ec5","timestamp":1358163946838,"operation":"GET","tenant":"ntua","container":"foo-container","originating-cluster":"vision-1","originating-machine":"10.0.0.6","transaction-latency":28,"user":"bill","transaction-duration":54}

public class GroupEventsTest {
    /**
     * 
     */
    private static class InMemoryEventDispatcher implements EventDispatcher {
        /**
         * Constructor.
         */
        public InMemoryEventDispatcher() {
        }


        /**
         * @see gr.ntua.vision.monitoring.dispatch.EventDispatcher#newEvent()
         */
        @Override
        public EventBuilder newEvent() {
            // TODO Auto-generated method stub
            return null;
        }


        /**
         * @see gr.ntua.vision.monitoring.dispatch.EventDispatcher#send()
         */
        @Override
        public void send() {
            // TODO Auto-generated method stub
        }
    }

    /***/
    private static final int        RULE_PERIOD = 3;
    /***/
    private final FakeObjectService obs         = new FakeObjectService(new InMemoryEventDispatcher());


    /***/
    @Test
    public void groupGeneratedEvents() {
    }


    /***/
    @Before
    public void setUp() {
    }
}
