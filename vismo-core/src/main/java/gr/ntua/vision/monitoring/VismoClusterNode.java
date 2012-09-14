package gr.ntua.vision.monitoring;

import java.util.ArrayList;


/**
 *
 */
public class VismoClusterNode implements VismoCloudElement {
    /***/
    private final EventSink              sink;
    /***/
    private final ArrayList<EventSource> sources = new ArrayList<EventSource>();


    /**
     * @param sink
     */
    public VismoClusterNode(final EventSink sink) {
        this.sink = sink;
    }


    /**
     * @see gr.ntua.vision.monitoring.VismoCloudElement#start()
     */
    @Override
    public void start() {
        // TODO Auto-generated method stub
    }
}
