package gr.ntua.vision.monitoring;

/**
 * A stoppable task is a thread that can be explicitly stopped. It falls in the implementer's hands how the task halt properly. It
 * is advised that each guarantee provided by the implementation be noted properly.
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
     * Halt the computation, relinquish any resources held by <code>this</code>.
     */
    public abstract void halt();
}
