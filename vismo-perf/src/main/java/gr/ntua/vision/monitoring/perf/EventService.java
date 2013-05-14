package gr.ntua.vision.monitoring.perf;

/**
 * 
 */
public interface EventService {
    /**
     * Send <code>noEvents</code> events of <code>size</code>bytes each.
     * 
     * @param noEvents
     *            the number of events to send.
     * @param size
     *            the size of each event in bytes.
     */
    void send(int noEvents, long size);


    /**
     * Send <code>noEvents</code> events of <code>size</code>bytes each, to the given <code>topic</code>.
     * 
     * @param topic
     *            the topic.
     * @param noEvents
     *            the number of events to send.
     * @param size
     *            the size of each event in bytes.
     */
    void send(String topic, int noEvents, long size);
}
