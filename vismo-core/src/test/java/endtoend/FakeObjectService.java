package endtoend;

import gr.ntua.vision.monitoring.zmq.VismoSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class FakeObjectService {
    /***/
    private static final Logger log = LoggerFactory.getLogger(FakeObjectService.class);
    /***/
    private final VismoSocket   sock;


    /**
     * @param sock
     */
    private FakeObjectService(final VismoSocket sock) {
        this.sock = sock;
        log.debug("using socket {}", sock);
    }


    public void sendEvent() {

    }
}
