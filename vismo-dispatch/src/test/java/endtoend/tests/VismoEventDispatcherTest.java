package endtoend.tests;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.dispatch.VismoEventDispatcher;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ZContext;


/**
 *
 */
public class VismoEventDispatcherTest {
    /***/
    @SuppressWarnings("serial")
    private static final Properties  props              = new Properties() {
                                                            {
                                                                setProperty("producers.point", "tcp://127.0.0.1:56429");
                                                                setProperty("consumers.port", "56430");
                                                                setProperty("udp.port", "56431");
                                                                setProperty("cluster.name", "foo");
                                                            }
                                                        };
    /***/
    private final VismoConfiguration conf               = new VismoConfiguration(props);
    /***/
    private final int                NO_EXPECTED_EVENTS = 10;
    /***/
    private FakeEventProducer        producer;
    /** the socket factory. */
    private final ZMQFactory         socketFactory      = new ZMQFactory(new ZContext());
    /***/
    private FakeVismoInstance        vismo;


    /***/
    @Before
    public void setUp() {
        vismo = new FakeVismoInstance(socketFactory.newBoundPullSocket(conf.getProducersPoint()), NO_EXPECTED_EVENTS);
        vismo.start();

        final VismoEventDispatcher d = new VismoEventDispatcher(socketFactory, conf, VismoEventDispatcherTest.class.getName());
        producer = new FakeEventProducer(d, NO_EXPECTED_EVENTS);
    }


    /**
     * 
     */
    @After
    public void tearDown() {
        vismo.hasReceivedAllEvents();
    }


    /**
     * @throws InterruptedException
     */
    @Test
    public void vismoReceivesEventsThroughDispatcher() throws InterruptedException {
        producer.sendEvents();
        waitEventsDelivery();
    }


    /**
     * @throws InterruptedException
     */
    private static void waitEventsDelivery() throws InterruptedException {
        Thread.sleep(1000);
    }
}
