package unit.tests;

import gr.ntua.vision.monitoring.dispatch.EventBuilder;
import gr.ntua.vision.monitoring.dispatch.EventDispatcher;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.rules.PeriodicRule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import integration.tests.FakeObjectService;

import java.util.List;
import java.util.Random;


/**
 * 
 */

// {"transaction-throughput":12821.185185185184,"status":"SUCCESS","object":"bar-object","type":"read","content-size":692344,"originating-service":"fake-obs","id":"ed6f7609-5ce4-433d-bf53-9a79ddad5ec5","timestamp":1358163946838,"operation":"GET","tenant":"ntua","container":"foo-container","originating-cluster":"vision-1","originating-machine":"10.0.0.6","transaction-latency":28,"user":"bill","transaction-duration":54}

public class RuleAggregationTest {
    /**
     * 
     */
    private static class AggregateOnContentSizeRule extends PeriodicRule {
        /**
         * Constructor.
         * 
         * @param engine
         * @param period
         */
        public AggregateOnContentSizeRule(final VismoRulesEngine engine, final long period) {
            super(engine, period);
        }


        /**
         * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
         */
        @Override
        public void performWith(final MonitoringEvent e) {
            // TODO Auto-generated method stub

        }


        /**
         * @see gr.ntua.vision.monitoring.rules.PeriodicRule#aggregate(java.util.List)
         */
        @Override
        protected MonitoringEvent aggregate(final List<MonitoringEvent> eventList) {
            // TODO Auto-generated method stub
            return null;
        }
    }


    /**
     * 
     */
    private static class MyEventDispatcher implements EventDispatcher {
        /**
         * Constructor.
         */
        public MyEventDispatcher() {
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
    private static final long       SEED = 513;
    /***/
    private final FakeObjectService obs  = new FakeObjectService(new MyEventDispatcher());
    /***/
    private final Random            rng  = new Random(SEED);
}
