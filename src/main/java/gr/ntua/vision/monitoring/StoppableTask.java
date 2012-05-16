package gr.ntua.vision.monitoring;

/**
 * A stoppable task is a thread that can be explicitly stopped. It falls in the implementer's hands how the task should accomplish
 * a proper shutdown sequence. Each guarantee given by the implementation should be noted.
 */
public class StoppableTask extends Thread {
    /**
     * @param name
     */
    public StoppableTask(final String name) {
        super(name);
    }


    /**
     * Shut down the task. The default implementation just interrupts the thread. In the general case, there is no guarantee that
     * a blocked thread will stop its execution.
     */
    public void shutDown() {
        interrupt();
    }
}
