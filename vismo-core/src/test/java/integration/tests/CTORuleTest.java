package integration.tests;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.VismoVMInfo;
import gr.ntua.vision.monitoring.dispatch.VismoEventDispatcher;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.rules.CTORule;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.service.ClusterHeadNodeFactory;
import gr.ntua.vision.monitoring.service.VismoService;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sinks.InMemoryEventSink;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ZContext;


/**
 * 
 */
public class CTORuleTest {
    /***/
    @SuppressWarnings("serial")
    private static final Properties  p          = new Properties() {
                                                    {
                                                        setProperty("cloud.name", "visioncloud.eu");
                                                        setProperty("cloud.heads", "10.0.2.211, 10.0.2.212");

                                                        setProperty("cluster.name", "vision-1");
                                                        setProperty("cluster.head", "10.0.2.211");

                                                        setProperty("producers.point", "tcp://127.0.0.1:56429");
                                                        setProperty("consumers.port", "56430");

                                                        setProperty("udp.port", "56431");
                                                        setProperty("cluster.head.port", "56432");

                                                        setProperty("cloud.head.port", "56433");

                                                        setProperty("mon.group.addr", "228.5.6.7");
                                                        setProperty("mon.group.port", "12345");
                                                        setProperty("mon.ping.period", "60000");
                                                    }
                                                };
    /***/
    private static final int         PERIOD     = 500;
    /***/
    private static final String      TOPIC      = "cto";
    /***/
    final ArrayList<MonitoringEvent> eventStore = new ArrayList<MonitoringEvent>();
    /***/
    private VismoConfiguration       conf;
    /***/
    private FakeObjectService        obs;
    /***/
    private VismoService             service;
    /***/
    private ZMQFactory               socketFactory;


    /**
     * @throws IOException
     */
    @Before
    public void setUp() throws IOException {
        conf = new VismoConfiguration(p);
        socketFactory = new ZMQFactory(new ZContext());
        obs = new FakeObjectService(new VismoEventDispatcher(socketFactory, conf, "fake-obs"));
        service = (VismoService) new ClusterHeadNodeFactory(conf, socketFactory) {
            /**
             * @see gr.ntua.vision.monitoring.service.ClusterHeadNodeFactory#getEventSinks()
             */
            @Override
            protected List< ? extends EventSink> getEventSinks() {
                final List<EventSink> sinks = new ArrayList<EventSink>();

                sinks.addAll(super.getEventSinks());
                sinks.add(new InMemoryEventSink(eventStore));

                return sinks;
            }


            /**
             * @see gr.ntua.vision.monitoring.service.ClusterHeadNodeFactory#submitRules(gr.ntua.vision.monitoring.rules.VismoRulesEngine)
             */
            @Override
            protected void submitRules(final VismoRulesEngine engine) {
                new CTORule(engine, TOPIC, PERIOD).submit();
                super.submitRules(engine);
            }
        }.build(new VismoVMInfo());
    }


    /**
     * @throws InterruptedException
     */
    @Test
    public void shouldReceiveCTOAggregationResult() throws InterruptedException {
        final VismoEventRegistry reg = new VismoEventRegistry(socketFactory, "tcp://127.0.0.1:" + conf.getConsumersPort());
        final CountDownLatch latch = new CountDownLatch(1);
        final ConsumerHandler handler = new ConsumerHandler(latch, 1);

        reg.register(TOPIC, handler);
        service.start();

        sendEvents(10);
        latch.await(900, TimeUnit.MILLISECONDS);
        assertGotExpectedEvent(handler);
    }


    /***/
    @After
    public void tearDown() {
        if (service != null)
            service.halt();
    }


    /**
     * @param handler
     */
    private void assertGotExpectedEvent(final ConsumerHandler handler) {
        assertEquals(1, handler.getNoReceivedEvents());
        assertEquals(TOPIC, eventStore.get(eventStore.size() - 1).topic());
    }


    /**
     * @param no
     */
    private void sendEvents(final int no) {
        for (int i = 0; i < no; ++i)
            obs.putEvent("ntua", "bill", "foo-container", "bar-object").send();
    }
}
