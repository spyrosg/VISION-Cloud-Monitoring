package gr.ntua.vision.monitoring;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;


/**
 * Provides basic information of the running vm.
 */
public interface VMInfo {
    /**
     * Given the public interface provided by {@link #getInterface()}, return its first non inet6 address.
     * 
     * @return the first non inet6 address, or <code>null</code> when no such address is found.
     * @throws SocketException
     */
    InetAddress getAddress() throws SocketException;


    /**
     * Try to get the name of the first public, not loop-back interface that is up on the host machine.
     * 
     * @return the first public interface, or <code>null</code> when no such interface exists.
     * @throws SocketException
     */
    NetworkInterface getInterface() throws SocketException;


    /**
     * @return the pid of the running jvm.
     * @throws Error
     *             when the pid is not available for this jvm.
     */
    int getPID();
}
