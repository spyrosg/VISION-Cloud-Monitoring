package gr.ntua.vision.monitoring;

import java.net.InetAddress;


/**
 * Provides basic information of the running vm.
 */
public interface VMInfo {
    /**
     * Return the first non inet6 address of this machine.
     * 
     * @return the first non inet6 address, or <code>null</code> when no such address is found.
     */
    InetAddress getAddress();


    /**
     * @return the pid of the running jvm.
     * @throws Error
     *             when the pid is not available for this jvm.
     */
    int getPID();
}
