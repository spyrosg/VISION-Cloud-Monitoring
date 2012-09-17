package gr.ntua.vision.monitoring;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class VismoClusterHead implements VismoCloudElement {
    /***/
    private static final Logger     log = LoggerFactory.getLogger(VismoClusterHead.class);
    /***/
    private final EventSink         sink;
    /***/
    private final List<EventSource> sources;


    /**
     * @param sink
     * @param sources
     */
    public VismoClusterHead(final EventSink sink, final List<EventSource> sources) {
        this.sink = sink;
        this.sources = sources;
    }


    /**
     * @see gr.ntua.vision.monitoring.VismoCloudElement#start()
     */
    @Override
    public void start() {
        logStartup();
    }


    /**
     * 
     */
    private void logStartup() {
        log.debug("listing source nodes:");

        for (final EventSource source : sources)
            log.debug("\t{}", source);
    }
}
