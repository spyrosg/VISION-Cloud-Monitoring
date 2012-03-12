package gr.ntua.vision.monitoring.lifecycle;

/**
 * A supervisor is used to keep track of all running threads in the monitoring instance and to properly shut them down (and also
 * the running application).
 */
public interface Supervisor {
    /**
     * Start the supervisor. This is the main entry point to the application.
     */
    void start();


    /**
     * Stop the supervisor and the running application.
     */
    void stop();
}
