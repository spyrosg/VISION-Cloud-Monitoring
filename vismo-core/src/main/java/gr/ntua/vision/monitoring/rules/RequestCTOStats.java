package gr.ntua.vision.monitoring.rules;

import java.util.ArrayList;


/**
 * An object used to hold all the CTO necessary stats per request.
 */
class RequestCTOStats {
    /***/
    private long                    containerAccesses;
    /***/
    private long                    contentSizeSum;
    /***/
    private final ArrayList<Double> reThinkTimes     = new ArrayList<Double>();
    /***/
    private final ArrayList<Double> thinkTimes       = new ArrayList<Double>();
    /***/
    private final ArrayList<Double> transactionTimes = new ArrayList<Double>();


    /**
     * Constructor.
     * 
     * @param contentSizeSum
     */
    public RequestCTOStats(final long contentSizeSum) {
        this.contentSizeSum = contentSizeSum;
        this.containerAccesses = 1;
    }


    /**
     * @param tt
     */
    public void addReThinkTime(final double tt) {
        reThinkTimes.add(tt);
    }


    /**
     * @param tt
     */
    public void addThinkTime(final double tt) {
        thinkTimes.add(tt);
    }


    /**
     * @param tt
     */
    public void addTransactionTime(final Double tt) {
        if (tt != null)
            transactionTimes.add(tt);
    }


    /**
     * @return the total number of bytes accessed in the container.
     */
    public long getContentSizeSum() {
        return contentSizeSum;
    }


    /**
     * @return the total number of accesses of the container.
     */
    public long getNoOfContainerAccesses() {
        return containerAccesses;
    }


    /**
     * @return the number of re-think-time requests.
     */
    public int getReThinkTimesCount() {
        return getCounts(reThinkTimes);
    }


    /**
     * @return the number of think-time requests.
     */
    public int getThinkTimesCount() {
        return getCounts(thinkTimes);
    }


    /**
     * Increase by one the total number of accesses on this container.
     */
    public void incAccesses() {
        ++containerAccesses;
    }


    /**
     * @param size
     */
    public void sumContentSize(final long size) {
        contentSizeSum += size;
    }


    /**
     * @return the sum of the time difference for each re-think request.
     */
    public double sumReThinkTimes() {
        return sumList(reThinkTimes);
    }


    /**
     * @return the sum of the time difference for each think request.
     */
    public double sumThinkTimes() {
        return sumList(thinkTimes);
    }


    /**
     * @return the sum of transaction time for all requests.
     */
    public double sumTransactionTimes() {
        return sumList(transactionTimes);
    }


    /**
     * @param l
     * @return the no of elements in the list.
     */
    private static int getCounts(final ArrayList<Double> l) {
        return l.size();
    }


    /**
     * @param list
     * @return the sum of the elements in the list.
     */
    private static double sumList(final ArrayList<Double> list) {
        double sum = 0;

        for (final double d : list)
            sum += d;

        return sum;
    }
}
