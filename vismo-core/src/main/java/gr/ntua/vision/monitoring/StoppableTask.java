package gr.ntua.vision.monitoring;

/**
 * A stoppable task is a thread that can be explicitly stopped. It falls in the implementer's hands how the task should accomplish
 * a proper shutdown sequence. It is advised that each guarantee provided by the implementation be noted properly.
 */
public abstract class StoppableTask extends Thread {
    /**
     * Constructor.
     * 
     * @param name
     *            the thread name.
     */
    public StoppableTask(final String name) {
        super(name);
    }


    /**
     * Shut down the task.
     */
    public abstract void shutDown();
}
