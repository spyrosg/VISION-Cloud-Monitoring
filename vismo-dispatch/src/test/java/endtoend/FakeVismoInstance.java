package endtoend;

import static org.junit.Assert.assertEquals;

import org.zeromq.ZMQ.Socket;


/**
 *
 */
public class FakeVismoInstance extends Thread {
    /***/
    private final int    noExpectedEvents;
    /***/
    private int          noReceivedEvents = 0;
    /***/
    private final Socket sock;


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
            final byte[] buf = sock.recv(0);

            if (buf == null)
                continue;

            final String msg = new String(buf, 0, buf.length);
            System.err.println("received: " + msg);

            ++noReceivedEvents;
        }
    }
}
