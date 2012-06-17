package endtoend;

import gr.ntua.vision.monitoring.VismoEventDispatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 *
 */
public class VismoEventDispatcherTest {
    /***/
    private FakeEventProducer    producer;
    /***/
    private VismoEventDispatcher dispatcher;
    /***/
    private FakeVismoInstance    vismo;


    /**
     * 
     */
    @Before
    public void setUp() {
        dispatcher = new VismoEventDispatcher("foo-bar");
        producer = new FakeEventProducer(dispatcher);
        vismo = new FakeVismoInstance();
        vismo.start();
    }


    /***/
    @Test
    public void vismoReceivesEventsThroughDispatcher() {
        producer.sendEvents();
    }


    /**
     * 
     */
    @After
    public void tearDown() {
        vismo.hasReceivedAllEvents();
        vismo.stop();
    }
}
