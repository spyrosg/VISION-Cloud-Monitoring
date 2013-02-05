package gr.ntua.vision.monitoring.dispatch;

/**
 * This is used to build events piecewise and eventually send it out.
 */
public interface EventBuilder {
    /**
     * Append a new field (key/value) pair to the event.
     * 
     * @param key
     *            the key.
     * @param value
     *            the value.
     * @return <code>this</code>.
     */
    EventBuilder field(String key, Object value);


    /**
     * Send the event down the socket.
     */
    void send();
}
