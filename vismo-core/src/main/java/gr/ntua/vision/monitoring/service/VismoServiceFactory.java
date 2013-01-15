package gr.ntua.vision.monitoring.service;

import gr.ntua.vision.monitoring.VMInfo;

import java.io.IOException;


/**
 * This is used to build up a service instance.
 */
public interface VismoServiceFactory {
    /**
     * This maybe used by client code to further configure the service.
     * 
     * @param service
     *            the service.
     */
    void bootstrap(Service service);


    /**
     * Build and setup a {@link Service} instance.
     * 
     * @param vminfo
     *            the vm info object.
     * @return an instance ready to run.
     * @throws IOException
     */
    Service build(VMInfo vminfo) throws IOException;
}
