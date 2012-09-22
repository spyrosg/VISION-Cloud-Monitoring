package endtoend;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.zmq.VismoSocket;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;


/**
 * 
 */
public class FakeObs {
    /***/
    private final Logger      log = LoggerFactory.getLogger(FakeObs.class);
    /***/
    private final VismoSocket sock;


    /**
     * Constructor.
     * 
     * @param configFile
     * @throws IOException
     * @throws FileNotFoundException
     */
    public FakeObs(final String configFile) throws FileNotFoundException, IOException {
        final Properties p = new Properties();

        p.load(new BufferedInputStream(new FileInputStream(configFile)));

        final VismoConfiguration conf = new VismoConfiguration(p);
        final ZMQSockets zmq = new ZMQSockets(new ZContext());

        this.sock = zmq.newConnectedPushSocket("tcp://127.0.0.1:" + conf.getProducersPort());
        log.debug("sending events to {}", this.sock);
    }


    /**
     * @param e
     */
    private void sendEvent(final ObsEvent e) {
        final String msg = e.toJSON().toString();

        log.trace("sending => {}", msg);
        sock.send(msg);
    }


    /**
     */
    private void start() {
        final String tenant = "ntua";
        final String user = "vassilis";
        final String container1 = "test-container";
        final String container2 = "2-container";
        final String object = "foo";

        sendEvent(getReadEvent(tenant, user, container1, object, 1000));
        sendEvent(getReadEvent(tenant, user, container1, object, 1000));
        sendEvent(getReadEvent(tenant, user, container1, object, 1000));

        sendEvent(getReadEvent(tenant, user, container2, object, 500));
        sendEvent(getReadEvent(tenant, user, container2, object, 500));
        sendEvent(getReadEvent(tenant, user, container2, object, 500));
    }


    /**
     * @param args
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void main(final String... args) throws FileNotFoundException, IOException {
        new FakeObs(args[0]).start();
    }


    /**
     * @param tenant
     * @param user
     * @param container
     * @param object
     * @param size
     * @return
     */
    private static ObsEvent getReadEvent(final String tenant, final String user, final String container, final String object,
            final long size) {
        return new GETObsEvent(tenant, user, container, object, size);
    }


    /**
     * @param tenant
     * @param user
     * @param container
     * @param object
     * @param size
     * @return
     */
    private static ObsEvent getWriteEvent(final String tenant, final String user, final String container, final String object,
            final long size) {
        return new PUTObsEvent(tenant, user, container, object, size);
    }
}
