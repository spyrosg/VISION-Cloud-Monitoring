package endtoend;

import java.net.SocketException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ZContext;

import examples.FakeEventConsumer;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.notify.EventRegistry;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;


/**
 * This test is used to demonstrate/verify Vismo's functionality in a simulated <strong>one node</strong> environment. For this
 * purpose, a local event producer is used ({@link FakeObjectService}), the vismo main program, and a {@link FakeEventConsumer}.
 * The workflow goes on like this:
 * <ol>
 * <li>Setting up the three different components, each running in each own thread.</li>
 * <li>{@link FakeObjectService} generates and sends a number of events to the vismo.</li>
 * <li>Vismo is responsible for aggregating the events for some period of time.</li>
 * <li>Finally, {@link FakeEventConsumer} receives and verifies the events received.</li>
 * </ol>
 */
public class VismoOneNodeEndToEndTest {
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
    private VismoDriver              driver;
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

        setupRegistry();
        setupConsumer();
        setupProducer(zmq);

        driver = new VismoDriver(conf);
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


    /***/
    private void setupRegistry() {
        registry = new EventRegistry(conf.getConsumersPoint());
    }


    /**
     * @throws InterruptedException
     */
    private static void waitForAllEventsToBeReceived() throws InterruptedException {
        Thread.sleep(1100);
    }
}
