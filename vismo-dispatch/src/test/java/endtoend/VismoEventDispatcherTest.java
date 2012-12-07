package endtoend;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.VismoEventDispatcher;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

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
    private static final Properties  props                        = new Properties() {
                                                                      {
                                                                          setProperty("producers.point", "tcp://127.0.0.1:56429");
                                                                          setProperty("consumers.port", "56430");
                                                                          setProperty("udp.port", "56431");
                                                                      }
                                                                  };
    /***/
    private static final String      VISMO_CONFIG_SYSTEM_PROPERTY = "vismo.config.properties";
    /***/
    private final VismoConfiguration conf                         = new VismoConfiguration(props);
    /***/
    private final int                NO_EXPECTED_EVENTS           = 10;
    /***/
    private FakeEventProducer        producer;
    /***/
    private FakeVismoInstance        vismo;
    /***/
    private final ZMQSockets         zmq                          = new ZMQSockets(new ZContext());

    static {
        System.setProperty(VISMO_CONFIG_SYSTEM_PROPERTY, "src/test/resources/config.properties");
    }


    /***/
    @Before
    public void setUp() {
        vismo = new FakeVismoInstance(zmq.newBoundPullSocket(conf.getProducersPoint()), NO_EXPECTED_EVENTS);
        vismo.start();
        final VismoEventDispatcher d = new VismoEventDispatcher(VismoEventDispatcherTest.class.getName());
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
        waitForAllEventsToBeReceived();
    }


    /**
     * @throws InterruptedException
     */
    private static void waitForAllEventsToBeReceived() throws InterruptedException {
        Thread.sleep(1000);
    }
}
