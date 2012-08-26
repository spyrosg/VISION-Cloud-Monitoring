package endtoend;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.notify.EventRegistry;
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
public class VismoEndToEndTest {
    /** the maximum number of events to sent for the test. */
    private static final int         NO_EVENTS_TO_SENT    = 10;
    /***/
    @SuppressWarnings("serial")
    private static final Properties  props                = new Properties() {
                                                              {
                                                                  setProperty("producers.point", "tcp://127.0.0.1:34890");
                                                                  setProperty("consumers.point", "tcp://127.0.0.1:34891");
                                                                  setProperty("udp.port", "34892");
                                                              }
                                                          };
    /***/
    private final VismoConfiguration conf                 = new VismoConfiguration(props);
    /***/
    private MonitoringDriver         driver;
    /***/
    private final EventCountHandler  eventConsumerCounter = new EventCountHandler(NO_EVENTS_TO_SENT);
    /***/
    private FakeEventProducer        eventProducer;
    /***/
    private EventRegistry            registry;


    /**
     * @throws Exception
     */
    @Test
    public void monitoringReceivesEventsFromEventProducer() throws Exception {
        driver.start();
        driver.reportsMonitoringStatus(conf.getUDPPort());
        eventProducer.sendEvents();
        waitForAllEventsToBeReceived();
        driver.reportsMonitoringStatus(conf.getUDPPort());
        driver.shutdown();
    }


    /**
     * @throws SocketException
     */
    @Before
    public void setUp() throws SocketException {
        final ZMQSockets zmq = new ZMQSockets(new ZContext());

        setupRegistry(zmq);
        setupConsumer();
        setupProducer(zmq);

        driver = new MonitoringDriver(conf);
        driver.setup();
    }


    /***/
    @After
    public void tearDown() {
        eventConsumerCounter.haveReceivedEnoughMessages();
        eventProducer.stop();
    }


    /***/
    private void setupConsumer() {
        registry.registerToAll(eventConsumerCounter);
    }


    /**
     * @param zmq
     */
    private void setupProducer(final ZMQSockets zmq) {
        eventProducer = new FakeEventProducer(zmq.newConnectedPushSocket(conf.getProducersPoint()), NO_EVENTS_TO_SENT);
    }


    /**
     * @param zmq
     */
    private void setupRegistry(final ZMQSockets zmq) {
        registry = new EventRegistry(zmq, conf.getConsumersPoint());
    }


    /**
     * @throws InterruptedException
     */
    private static void waitForAllEventsToBeReceived() throws InterruptedException {
        Thread.sleep(1100);
    }
}
