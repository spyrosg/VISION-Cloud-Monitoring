package gr.ntua.vision.monitoring.policy;

import gr.ntua.vision.monitoring.VMInfo;
import gr.ntua.vision.monitoring.service.Service;

import java.io.IOException;


/**
 * This is used to abstract away the decision or strategy that provides a specific instance to the vismo mesh.
 */
public interface NodePolicy {
    /**
     * Configure and return the vismo service instance.
     * 
     * @param vminfo
     * @return a configured {@link Service} ready to run.
     * @throws IOException
     */
    Service build(final VMInfo vminfo) throws IOException;
}
