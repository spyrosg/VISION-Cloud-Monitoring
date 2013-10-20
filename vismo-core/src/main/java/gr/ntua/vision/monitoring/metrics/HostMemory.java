package gr.ntua.vision.monitoring.metrics;

/**
*
*/
public class HostMemory {
    /***/
    public final long total;
    /***/
    public final long used;


    /**
     * @param total
     * @param used
     */
    public HostMemory(final long total, final long used) {
        this.total = total;
        this.used = used;
    }
}
