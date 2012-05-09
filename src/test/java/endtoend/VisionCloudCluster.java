package endtoend;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 *
 */
public class VisionCloudCluster {
    /** The no of milliseconds we wait the application to join the cluster. */
    private static final int   JOIN_TIMEOUT = 1000;
    /***/
    private final ZContext     ctx          = new ZContext();
    /***/
    private final List<String> machines     = new ArrayList<String>();
    /***/
    private final Socket       s;


    /**
     * 
     */
    public VisionCloudCluster() {
        this.s = ctx.createSocket(ZMQ.REP);
        this.s.setReceiveTimeOut(JOIN_TIMEOUT);
        this.s.setLinger(0); // reject messages in the queue.
    }


    /**
     * 
     */
    public void receivesEvents() {
        fail("NYI");
    }


    /**
     * Wait with timeout for a new join request.
     */
    public void receivesNewJoin() {
        final String req = receive();

        if (req == null)
            fail("application did not join");

        System.out.println("received " + req);
    }


    /**
     * 
     */
    public void shutDown() {
        s.close();
    }


    /**
     * 
     */
    public void start() {
        s.bind("ipc://hellostream:5678");

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
}
