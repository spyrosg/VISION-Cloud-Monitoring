package endtoend;

import static org.junit.Assert.assertEquals;
import gr.ntua.monitoring.sockets.Socket;

import java.util.logging.Logger;


/**
 *
 */
public class FakeVismoInstance extends Thread {
    /***/
    private static final Logger log              = Logger.getLogger(FakeVismoInstance.class.getName());
    /***/
    private final int           noExpectedEvents;
    /***/
    private int                 noReceivedEvents = 0;
    /***/
    private final Socket        sock;


    /**
     * @param sock
     * @param noExpectedEvents
     */
    public FakeVismoInstance(final Socket sock, final int noExpectedEvents) {
        super("fake-vismo-instance");
        this.sock = sock;
        this.noExpectedEvents = noExpectedEvents;
    }


    /***/
    public void hasReceivedAllEvents() {
        assertEquals(noExpectedEvents, noReceivedEvents);
    }


    /***/
    @Override
    public void run() {
        System.err.println("entering receive loop");

        while (true) {
            final String msg = sock.receive();

            if (msg == null)
                continue;

            log.config("received: " + msg);
            ++noReceivedEvents;
        }
    }
}
