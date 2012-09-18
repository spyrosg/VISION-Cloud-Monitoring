package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;

import java.net.SocketException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class VismoClusterHead extends AbstractVismoCloudElement {
    /***/
    private static final Logger log = LoggerFactory.getLogger(VismoClusterHead.class);


    /**
     * @param vminfo
     * @param sink
     * @param sources
     * @throws SocketException
     */
    public VismoClusterHead(final VMInfo vminfo, final EventSink sink, final List<EventSource> sources) throws SocketException {
        super(vminfo, sink, sources);
    }


    /**
     * @see gr.ntua.vision.monitoring.EventListener#receive(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void receive(final Event e) {
        log.trace("received {}", e);
    }
}
