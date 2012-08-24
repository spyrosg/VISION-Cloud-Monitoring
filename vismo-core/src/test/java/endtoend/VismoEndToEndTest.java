package endtoend;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.notify.EventRegistry;

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
                                                                  setProperty("consumers.point", "ipc:///tmp/vismo.test.port");
                                                                  setProperty("udp.port", "56431");
                                                              }
                                                          };

    /***/
    private final VismoConfiguration conf                 = new VismoConfiguration(props);
    /***/
    private final ZContext           ctx                  = new ZContext();
    /***/
    private MonitoringDriver         driver;
    /***/
    private final EventCountHandler  eventConsumerCounter = new EventCountHandler(NO_EVENTS_TO_SENT);
    /***/
    private final FakeEventProducer  eventProducer        = new FakeEventProducer(ctx, conf.getProducersPoint(),
                                                                  NO_EVENTS_TO_SENT);
    /***/
    private final EventRegistry      registry             = new EventRegistry(ctx, conf.getConsumersPoint());


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
        driver = new MonitoringDriver();
        driver.setup(conf.getUDPPort(), ctx, conf.getProducersPoint(), conf.getConsumersPoint());
        eventProducer.start();
        setupConsumer();
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
     * @throws InterruptedException
     */
    private static void waitForAllEventsToBeReceived() throws InterruptedException {
        Thread.sleep(1100);
    }
}
