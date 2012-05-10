package endtoend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 *
 */
public class VisionCloudCluster {
    /** The no of milliseconds we wait the application to join the "cluster". */
    private static final int JOIN_TIMEOUT = 1000;
    /***/
    private final ZContext   ctx          = new ZContext();
    /***/
    private final Socket     s;


    /**
     * 
     */
    public VisionCloudCluster() {
        this.s = ctx.createSocket(ZMQ.REP);
        this.s.setReceiveTimeOut(JOIN_TIMEOUT);
        this.s.setLinger(0);
    }


    /**
     * 
     */
    public void receivesEvents() {
        final int EXPECTED_NO_EVENTS = 10;
        final Socket eventSocket = ctx.createSocket(ZMQ.REP);
        int receivedEventsNo = 0;

        eventSocket.setReceiveTimeOut(-1);
        eventSocket.setLinger(0);
        eventSocket.bind("ipc://events");

        while (true) {
            final byte[] buf = eventSocket.recv(0);

            if (buf == null)
                break;

            ++receivedEventsNo;
            eventSocket.send("bar".getBytes(), 0);

            if (receivedEventsNo >= EXPECTED_NO_EVENTS)
                break;
        }

        eventSocket.close();

        assertEquals(EXPECTED_NO_EVENTS, receivedEventsNo);
    }


    /**
     * Wait with timeout for a new join request.
     */
    public void receivesNewJoin() {
        final String req = receive();

        if (req == null)
            fail("application did not join");

        send("ohai");
    }


    /**
     * 
     */
    public void shutDown() {
        s.close();
    }


    /**
     * Start listening for incoming requests.
     */
    public void start() {
        s.bind("ipc://join");
    }


    /**
     * @return
     */
    private String receive() {
        final byte[] buf = s.recv(0);

        if (buf == null)
            return null;

        return new String(buf, 0, buf.length);
    }


    /**
     * @param message
     */
    private void send(final String message) {
        s.send(message.getBytes(), 0);
    }
}
