package endtoend;

import gr.ntua.vision.monitoring.EventDispatcher;

import java.net.SocketException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zeromq.ZContext;


/**
 *
 */
public class VismoEventDispatcherTest {
    /***/
    private final ZContext    ctx                = new ZContext();
    /***/
    private EventDispatcher   dispatcher;
    /***/
    private final String      LOCAL_EVENTS_PORT  = "ipc:///tmp/dispatch-events-test";
    /***/
    private final int         NO_EXPECTED_EVENTS = 10;
    /***/
    private FakeEventProducer producer;
    /***/
    private FakeVismoInstance vismo;


    /**
     * @throws SocketException
     */
    @Before
    public void setUp() throws SocketException {
        vismo = new FakeVismoInstance(ctx, LOCAL_EVENTS_PORT, NO_EXPECTED_EVENTS);
        vismo.start();
        dispatcher = new EventDispatcher(ctx, LOCAL_EVENTS_PORT, "foo-bar");
        producer = new FakeEventProducer(dispatcher, NO_EXPECTED_EVENTS);
    }


    /**
     * 
     */
    @After
    public void tearDown() {
        vismo.hasReceivedAllEvents();
        vismo.shutDown();
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
