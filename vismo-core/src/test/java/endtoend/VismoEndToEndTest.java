package endtoend;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.notify.EventRegistry;

import java.net.SocketException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 *
 */
public class VismoEndToEndTest {
    /***/
    private static final Logger      log                  = LoggerFactory.getLogger(VismoEndToEndTest.class);
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
    private final FakeEventProducer  eventProducer        = buildFakeEventProducer();
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
        driver = new MonitoringDriver(conf);
        driver.setup();
        setupConsumer();
    }


    /***/
    @After
    public void tearDown() {
        eventConsumerCounter.haveReceivedEnoughMessages();
        eventProducer.stop();
    }


    /**
     * @return
     */
    private FakeEventProducer buildFakeEventProducer() {
        final Socket sock = ctx.createSocket(ZMQ.PUSH);

        sock.setLinger(0);
        sock.connect(conf.getProducersPoint());
        log.debug("connecting to endpoint={}", conf.getProducersPoint());

        return new FakeEventProducer(sock, NO_EVENTS_TO_SENT);
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
