package endtoend.tests;

/***/
abstract class TimedCodeBlock {
    /***/
    private long endTime   = 0;
    /***/
    private long startTime = 0;


    /**
     * Constructor.
     */
    public TimedCodeBlock() {
    }


    /**
     * @return the execution duration of the code block.
     */
    public long getDuration() {
        return endTime - startTime;
    }


    /**
     * Run and time the application of the block.
     * 
     * @return <code>this</code>.
     */
    public TimedCodeBlock run() {
        markStart();
        withBlock();
        markEnd();

        return this;
    }


    /**
     * The block of code to run and time.
     */
    public abstract void withBlock();


    /***/
    protected void markEnd() {
        endTime = System.currentTimeMillis();
    }


    /***/
    protected void markStart() {
        startTime = System.currentTimeMillis();
    }
}
