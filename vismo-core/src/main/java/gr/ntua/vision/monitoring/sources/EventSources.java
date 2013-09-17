package gr.ntua.vision.monitoring.sources;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This provides a grouping for all event sources in the system.
 */
public class EventSources {
    /** the log target. */
    private static final Logger               log     = LoggerFactory.getLogger(EventSources.class);
    /** the sources set. */
    private final ArrayList<VismoEventSource> sources = new ArrayList<VismoEventSource>();


    /**
     * Append another source to the group.
     * 
     * @param source
     *            the source.
     * @return <code>this</code>.
     */
    public EventSources append(final VismoEventSource source) {
        sources.add(source);
        return this;
    }


    /**
     * Stop accepting events.
     */
    public void halt() {
        for (final VismoEventSource source : sources)
            try {
                log.debug("halting {}", source);
                source.halt();
            } catch (final Throwable x) {
                log.error("error", x);
            }
    }


    /**
     * Remove the source from the group.
     * 
     * @param source
     *            the source.
     * @return <code>this</code>.
     */
    public EventSources remove(final VismoEventSource source) {
        sources.remove(source);
        return this;
    }


    /**
     * Clear the sources group.
     */
    public void removeAll() {
        sources.clear();
    }


    /**
     * Start accepting events.
     */
    public void start() {
        for (final VismoEventSource source : sources) {
            log.debug("starting {}", source);
            source.start();
        }
    }


    /**
     * Subscribe listener to all available sources.
     * 
     * @param listener
     *            the listener.
     */
    public void subscribeAll(final EventSourceListener listener) {
        for (final EventSource source : sources)
            source.add(listener);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return sources.toString();
    }
}
