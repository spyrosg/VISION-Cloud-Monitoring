package gr.ntua.vision.monitoring;

/**
 * TODO: document and refactor to be used in project.
 */
public interface MonitoringSocket {
    /**
     * 
     */
    void close();


    /**
     * @return the message received.
     */
    String receive();


    /**
     * @param message
     * @return <code>true</code> when the message was successfully transmitted, <code>false</code> otherwise.
     */
    boolean send(String message);
}
