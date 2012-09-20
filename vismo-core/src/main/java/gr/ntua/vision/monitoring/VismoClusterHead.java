package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sources.BasicEventSource;

import java.net.SocketException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class VismoClusterHead extends AbstractVismoCloudElement {
    /***/
    private static final String DICT_KEY = "!dict";
    /***/
    private static final Logger log      = LoggerFactory.getLogger(VismoClusterHead.class);


    /**
     * @param vminfo
     * @param sink
     * @param sources
     * @throws SocketException
     */
    public VismoClusterHead(final VMInfo vminfo, final EventSink sink, final List<BasicEventSource> sources)
            throws SocketException {
        super(vminfo, sink, sources);
    }


    /**
     * @see gr.ntua.vision.monitoring.EventListener#receive(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void receive(final Event e) {
        @SuppressWarnings("rawtypes")
        final Map map = (Map) e.get(DICT_KEY);

        log.trace("received event from {}: {}", map.get("originating-machine"), map);
        aggregate();
        send(e);
    }


    /**
     * @see gr.ntua.vision.monitoring.AbstractVismoCloudElement#log()
     */
    @Override
    protected Logger log() {
        return log;
    }


    /**
     * 
     */
    private void aggregate() {
        // TODO Auto-generated method stub

    }
}
