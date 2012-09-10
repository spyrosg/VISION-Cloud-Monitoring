package endtoend;

import gr.ntua.vision.monitoring.Vismo;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.VismoFactory;

import java.net.SocketException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * This test is used to demonstrate/verify Vismo's functionality in a simulated <strong>one node</strong> environment. For this
 * purpose, a local event producer is used ({@link FakeObjectService}), the vismo main program, and a
 * {@link EventVerifyingConsumer}. The workflow goes on like this:
 * <ol>
 * <li>Setting up the three different components, each running in each own thread.</li>
 * <li>{@link FakeObjectService} generates and sends a number of events to the vismo.</li>
 * <li>Vismo is responsible for aggregating the events for some period of time.</li>
 * <li>Finally, {@link EventVerifyingConsumer} receives and verifies the events received.</li>
 * </ol>
 */
public class VismoOneNodeEndToEndTest {
    /***/
    private static final int         NO_EVENTS = 100;
    /***/
    @SuppressWarnings("serial")
    private static final Properties  props     = new Properties() {
                                                   {
                                                       setProperty("producers.point", "inproc://vismo-1-node-test.producers");
                                                       setProperty("consumers.point", "inproc://vismo-1-node-test.consumers");
                                                       setProperty("udp.port", "34892");
                                                   }
                                               };
    /***/
    private final VismoConfiguration conf      = new VismoConfiguration(props);
    /** the event consumer instance. */
    private EventVerifyingConsumer   consumer;
    /** the object service producer instance */
    private FakeObjectService        obs;
    /** the vismo instance. */
    private Vismo                    vismo;


    /**
     * @throws Exception
     */
    @Test
    public void eventsGenerationAndDistribution() throws Exception {
        obs.sendEvents();
        waitForEventsToBeProcessed();
        consumer.hasVerifiedEvents();
    }


    /**
     * @throws SocketException
     */
    @Before
    public void setUp() throws SocketException {
        setupObs();
        setupVismo();
        setupConsumer();
    }


    /***/
    @After
    public void tearDown() {
        if (consumer != null)
            consumer.stop();

        if (vismo != null)
            vismo.stop();

        if (obs != null)
            obs.stop();
    }


    /***/
    private void setupConsumer() {
        consumer = new EventVerifyingConsumer(NO_EVENTS);
        consumer.start();
    }


    /***/
    private void setupObs() {
        obs = new FakeObjectService(NO_EVENTS);
        obs.start();
    }


    /**
     * @throws SocketException
     */
    private void setupVismo() throws SocketException {
        vismo = new VismoFactory(conf).build();
        vismo.start();
    }


    /**
     * @throws InterruptedException
     */
    private static void waitForEventsToBeProcessed() throws InterruptedException {
        Thread.sleep(1100);
    }
}
