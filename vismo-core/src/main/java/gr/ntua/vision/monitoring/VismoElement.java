package gr.ntua.vision.monitoring;

/**
 * A vismo hierarchy runs in a number of nodes and clusters (within a cloud). This hierarchy consists of elements. Each element is
 * responsible for driving the collection, aggregation and distribution of events.
 */
public interface VismoElement {
    /**
     * Start monitoring.
     */
    void start();
}
