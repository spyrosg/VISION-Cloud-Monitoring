package endtoend;

import static org.junit.Assert.assertEquals;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 *
 */
public class FakeVismoInstance extends Thread {
    /***/
    private final ZContext ctx;
    /***/
    private final String   localEventsPort;
    /***/
    private final int      noExpectedEvents;
    /***/
    private int            noReceivedEvents = 0;
    /***/
    private final Socket   sock;
    /***/
    private final String   STOP_COMMAND     = "STOP";


    /**
     * @param ctx
     * @param localEventsPort
     * @param noExpectedEvents
     */
    public FakeVismoInstance(final ZContext ctx, final String localEventsPort, final int noExpectedEvents) {
        super("fake-vismo-instance");
        this.ctx = ctx;
        this.localEventsPort = localEventsPort;
        this.noExpectedEvents = noExpectedEvents;
        this.sock = ctx.createSocket(ZMQ.PULL);
        this.sock.bind(localEventsPort);
        this.sock.setLinger(0);
        System.err.println("listening on port=" + localEventsPort);
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

            if (msg.equals(STOP_COMMAND))
                break;

            ++noReceivedEvents;
        }

        sock.close();
    }


    /***/
    public void shutDown() {
        final Socket termSocket = ctx.createSocket(ZMQ.PUSH);
        termSocket.connect(localEventsPort);
        termSocket.setLinger(0);
        termSocket.send(STOP_COMMAND.getBytes(), 0);
        termSocket.close();
    }
}
