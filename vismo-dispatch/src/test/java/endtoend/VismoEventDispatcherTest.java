package endtoend;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.VismoEventDispatcher;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.net.SocketException;
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
                                                                setProperty("producers.point", "tcp://127.0.0.1:34890");
                                                                setProperty("consumers.point", "tcp://127.0.0.1:34891");
                                                                setProperty("udp.port", "34892");
                                                            }
                                                        };
    /***/
    private final VismoConfiguration conf               = new VismoConfiguration(props);
    /***/
    private final int                NO_EXPECTED_EVENTS = 10;
    /***/
    private FakeEventProducer        producer;
    /***/
    private FakeVismoInstance        vismo;
    /***/
    private final ZMQSockets         zmq                = new ZMQSockets(new ZContext());


    /**
     * @throws SocketException
     */
    @Before
    public void setUp() throws SocketException {
        vismo = new FakeVismoInstance(zmq.newBoundPullSocket("tcp://127.0.0.1:" + conf.getProducersPort()), NO_EXPECTED_EVENTS);
        vismo.start();
        final VismoEventDispatcher d = new VismoEventDispatcher(VismoEventDispatcherTest.class.getName(),
                zmq.newConnectedPushSocket("tcp://*" + conf.getProducersPort()));
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
