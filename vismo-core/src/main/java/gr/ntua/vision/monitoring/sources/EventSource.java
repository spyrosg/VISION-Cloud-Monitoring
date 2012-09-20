package gr.ntua.vision.monitoring.sources;

import gr.ntua.vision.monitoring.EventListener;


/**
 * A source of events is responsible for receiving events, that then distributes to the various in-system consumers. Consumers
 * wanting to receive are expected to implement the {@link EventListener} api. The order and the threading considerations (
 * <code>blocking</code>, <code>non-blocking</code>) in the way {@link EventListener}s are notified is unspecified.
 */
public interface EventSource {
    /**
     * Subscribe the listener to the source.
     * 
     * @param listener
     *            the listener.
     */
    void subscribe(EventListener listener);
}
