package gr.ntua.vision.monitoring.rules.propagation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 */
public class HeartbeatReceiver {

    /**
     * A multicast receiver which continuously receives heartbeats.
     */
    private class MulticastReceiverProcessorThread extends Thread {

        /**
         * constructor
         */
        public MulticastReceiverProcessorThread() {

        }


        /***/
        @Override
        public final void interrupt() {
            super.interrupt();
        }


        /***/
        @Override
        public final void run() {
            while (!stopped) {

                try {
                    Thread.sleep(MEMBERSHIP_UPDATE_INTERVAL);
                } catch (final InterruptedException e) {
                    HeartbeatReceiver.log.debug("Multicast Processor Thread sleep interrupted");
                }
                updateHostsMembership(MEMBERSHIP_TIMEOUT);
                // HeartbeatReceiver.log.info("Memberlist: " + getMembers().toString());
            }
        }


        /**
         * Decide whether the host is active based on previous received timestamps.
         * 
         * @param maxtime
         * @param host
         * @return if the host is still active.
         */
        private boolean isActive(final long maxtime, final String host) {
            final long delta = System.currentTimeMillis() - getHostsTimestamp().get(host);
            return delta < maxtime;
        }


        /**
         * Update the membership.
         * 
         * @param maxtime
         */
        private void updateHostsMembership(final long maxtime) {
            final Iterator<String> iterator = getHostsTimestamp().keySet().iterator();
            while (iterator.hasNext()) {
                final String host = iterator.next().toString();
                if (isActive(maxtime, host))
                    getMembers().put(host, true);
                else
                    getMembers().remove(host);
            }
        }

    }


    /**
     * A multicast receiver which continuously receives heartbeats.
     */
    private class MulticastReceiverThread extends Thread {

        /**
         * constructor
         */
        public MulticastReceiverThread() {

        }


        /**
         * 
         */
        @Override
        public final void interrupt() {
            super.interrupt();
        }


        /**
         * 
         */
        @Override
        public final void run() {
            final byte[] buf = new byte[1000];
            try {
                while (!stopped) {
                    final DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    try {
                        socket.receive(packet);
                        processPacket(packet);

                    } catch (final IOException e) {
                        if (!stopped)
                            HeartbeatReceiver.log.debug("Error receiving heartbeat. " + e.getMessage() + ". Initial cause was "
                                    + e.getMessage(), e);
                    }
                }
            } catch (final Throwable t) {
                HeartbeatReceiver.log.debug("Multicast receiver thread caught throwable. Cause was " + t.getMessage()
                        + ". Continuing...");
            }
        }


        /**
         * updates the host timestamp.
         * 
         * @param packet
         */
        private void processPacket(final DatagramPacket packet) {
            getHostsTimestamp().put(packet.getAddress().toString().substring(1) + ":" + new String(packet.getData()).trim(),
                                    Long.valueOf(System.currentTimeMillis()));
        }
    }
    /***/
    static final Logger                              log                        = LoggerFactory
                                                                                        .getLogger(HeartbeatReceiver.class);
    /***/
    MulticastSocket                                  socket;
    /***/
    volatile boolean                                 stopped                    = false;
    /***/
    private final InetAddress                        groupMulticastAddress;
    /***/
    private final Integer                            groupMulticastPort;
    /***/
    private final ConcurrentHashMap<String, Boolean> hostsMembership            = new ConcurrentHashMap<String, Boolean>();
    /***/
    private final ConcurrentHashMap<String, Long>    hostsTimestamp             = new ConcurrentHashMap<String, Long>();
    /***/
    private final int                                MEMBERSHIP_TIMEOUT         = 2000;
    /***/
    private final int                                MEMBERSHIP_UPDATE_INTERVAL = 500;
    /***/
    private MulticastReceiverProcessorThread         processorThread;
    /***/
    private MulticastReceiverThread                  receiverThread;


    /**
     * Constructor.
     * 
     * @param multicastAddress
     * @param multicastPort
     */
    public HeartbeatReceiver(final InetAddress multicastAddress, final Integer multicastPort) {
        System.setProperty("Djava.net.preferIPv4Stack", "true");
        this.groupMulticastAddress = multicastAddress;
        this.groupMulticastPort = multicastPort;
    }


    /**
     * clears the memberships
     */
    public final void clearMembership() {
        getMembers().clear();
    }


    /**
     * @return host timestamp
     */
    public ConcurrentHashMap<String, Long> getHostsTimestamp() {
        return hostsTimestamp;
    }


    /**
     * @return members
     */
    public final ConcurrentHashMap<String, Boolean> getMembers() {
        return hostsMembership;
    }


    /**
     * Shutdown the heartbeat service.
     */
    public final void halt() {
        stopped = true;
        receiverThread.interrupt();
        processorThread.interrupt();

        try {
            socket.leaveGroup(groupMulticastAddress);
        } catch (final IOException e) {
            HeartbeatReceiver.log.debug("Error leaving group");
        }
        socket.close();

    }


    /**
     * Start the heartbeat service.
     * 
     * @throws IOException
     */
    public final void init() throws IOException {
        socket = new MulticastSocket(groupMulticastPort.intValue());
        socket.joinGroup(groupMulticastAddress);
        receiverThread = new MulticastReceiverThread();
        receiverThread.start();
        processorThread = new MulticastReceiverProcessorThread();
        processorThread.start();

    }

}
