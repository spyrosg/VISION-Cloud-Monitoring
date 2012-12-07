package gr.ntua.vision.monitoring.policy;

import gr.ntua.vision.monitoring.VMInfo;
import gr.ntua.vision.monitoring.VismoService;

/**
 * This is used to abstract away the decision or strategy that provides a specific instance to the vismo mesh.
 */
public interface NodePolicy {
    /**
     * Configure and return the vismo service instance.
     * 
     * @param vminfo
     * @return a configured {@link VismoService} ready to run.
     */
    VismoService build(final VMInfo vminfo);
}
