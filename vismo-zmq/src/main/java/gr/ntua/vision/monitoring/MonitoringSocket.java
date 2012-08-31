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
     * @return
     */
    String receive();


    /**
     * @param message
     * @return
     */
    boolean send(String message);
}
