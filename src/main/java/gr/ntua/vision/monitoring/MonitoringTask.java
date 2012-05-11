package gr.ntua.vision.monitoring;

/**
 *
 */
abstract class MonitoringTask extends Thread {
    /**
     * Constructor.
     * 
     * @param name
     */
    public MonitoringTask(final String name) {
        super(name);
    }


    /**
     * 
     */
    abstract void shutDown();
}
