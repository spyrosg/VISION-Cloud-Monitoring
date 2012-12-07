package gr.ntua.vision.monitoring.heartbeat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class HeartbeatReceiver {

    /**
     * A multicast receiver which continuously receives heartbeats.
     */
    private final class MulticastReceiverProcessorThread extends Thread {

        /*
         * 
         */
        @Override
        public final void interrupt() {
            super.interrupt();
        }


        @Override
        public final void run() {
            while (!stopped) {

                try {
                    Thread.sleep(HeartbeatReceiver.MEMBERSHIP_UPDATE_INTERVAL);
                } catch (final InterruptedException e) {
                    HeartbeatReceiver.log.debug("Multicast Processor Thread sleep interrupted");
                }
                updateHostsMembership(HeartbeatReceiver.MEMBERSHIP_TIMEOUT);
                log.info("Memberlist: " + hostsMembership.toString());
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
            final long delta = System.currentTimeMillis() - hostsTimestamp.get(host);
            return delta < maxtime;
        }


        /**
         * Update the membership.
         * 
         * @param maxtime
         */
        private void updateHostsMembership(final long maxtime) {
            final Iterator<String> iterator = hostsTimestamp.keySet().iterator();
            while (iterator.hasNext()) {
                final String host = iterator.next().toString();
                hostsMembership.put(host, isActive(maxtime, host));
            }
        }

    }


    /**
     * A multicast receiver which continuously receives heartbeats.
     */
    private final class MulticastReceiverThread extends Thread {

        /*
         * 
         */
        @Override
        public final void interrupt() {
            super.interrupt();
        }


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


        /*
         * updates the hosts timestamp
         */
        private void processPacket(final DatagramPacket packet) {
            hostsTimestamp.put(packet.getAddress().toString().substring(1), Long.valueOf(System.currentTimeMillis()));
        }
    }
    private static final Logger              log                        = LoggerFactory.getLogger(HeartbeatReceiver.class);
    private static final int                 MEMBERSHIP_TIMEOUT         = 2000;
    private static final int                 MEMBERSHIP_UPDATE_INTERVAL = 500;
    private final InetAddress                groupMulticastAddress;
    private final Integer                    groupMulticastPort;
    private final HashMap<String, Boolean>   hostsMembership            = new HashMap<String, Boolean>();
    private final HashMap<String, Long>      hostsTimestamp             = new HashMap<String, Long>();
    private MulticastReceiverProcessorThread processorThread;
    private MulticastReceiverThread          receiverThread;
    private MulticastSocket                  socket;
    private volatile boolean                 stopped                    = false;


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


    public final HashMap<String, Boolean> getMembers() {
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
    
    public final void clearMembership(){
        hostsMembership.clear();
    }

}
