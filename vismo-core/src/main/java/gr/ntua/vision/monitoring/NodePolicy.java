package gr.ntua.vision.monitoring;

/**
 * This is used to abstract away the decision or strategy that provides a specific instance to the vismo mesh.
 */
public interface NodePolicy {
    /**
     * @param vminfo
     * @return a configureed {@link VismoService} ready to run.
     */
    VismoService build(final VMInfo vminfo);
}
