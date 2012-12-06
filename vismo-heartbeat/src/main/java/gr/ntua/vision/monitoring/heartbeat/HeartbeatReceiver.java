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

    private static final Logger              log                        = LoggerFactory.getLogger(HeartbeatReceiver.class);
    private static final int                 MEMBERSHIP_TIMEOUT         = 1000;
    private static final int                 MEMBERSHIP_UPDATE_INTERVAL = 500;
    private HashMap<String, Long>            hostsTimestamp             = new HashMap<String, Long>();
    private HashMap<String, Boolean>         hostsMembership            = new HashMap<String, Boolean>();
    private final InetAddress                groupMulticastAddress;
    private final Integer                    groupMulticastPort;
    private MulticastReceiverThread          receiverThread;
    private MulticastReceiverProcessorThread processorThread;
    private MulticastSocket                  socket;
    private volatile boolean                 stopped                    = false;


    /**
     * Constructor.
     * 
     * @param multicastAddress
     * @param multicastPort
     */
    public HeartbeatReceiver(InetAddress multicastAddress, Integer multicastPort) {
        System.setProperty("Djava.net.preferIPv4Stack", "true");
        this.groupMulticastAddress = multicastAddress;
        this.groupMulticastPort = multicastPort;
    }


    /**
     * Start the heartbeat service.
     * 
     * @throws IOException
     */
    final void init() throws IOException {
        socket = new MulticastSocket(groupMulticastPort.intValue());
        System.out.println(socket);
        socket.joinGroup(groupMulticastAddress);
        receiverThread = new MulticastReceiverThread();
        receiverThread.start();
        processorThread = new MulticastReceiverProcessorThread();
        processorThread.start();

    }


    /**
     * Shutdown the heartbeat service.
     */
    public final void dispose() {
        log.info("dispose called");
        stopped = true;
        receiverThread.interrupt();
        processorThread.interrupt();

    }


    /**
     * A multicast receiver which continuously receives heartbeats.
     */
    private final class MulticastReceiverProcessorThread extends Thread {

        public final void run() {
            while (!stopped) {

                try {
                    Thread.sleep(MEMBERSHIP_UPDATE_INTERVAL);
                } catch (InterruptedException e) {
                    log.info("Sleep interrupted. Initial cause was " + e.getMessage(), e);
                }
                updateHostsMembership(MEMBERSHIP_TIMEOUT);
                System.out.println(hostsMembership);
            }
        }


        /**
         * Update the membership.
         * 
         * @param maxtime
         */
        private void updateHostsMembership(long maxtime) {
            Iterator<String> iterator = hostsTimestamp.keySet().iterator();
            while (iterator.hasNext()) {
                String host = iterator.next().toString();
                hostsMembership.put(host, isActive(maxtime, host));
            }
        }


        /**
         * Decide whether the host is active based on previous received timestamps.
         * 
         * @param maxtime
         * @param host
         * @return if the host is still active.
         */
        private boolean isActive(long maxtime, String host) {
            long delta = System.currentTimeMillis() - hostsTimestamp.get(host);
            return delta < maxtime;
        }


        /*
         * 
         */
        public final void interrupt() {
            try {
                socket.leaveGroup(groupMulticastAddress);
            } catch (IOException e) {
                log.info("Error leaving group");
            }
            socket.close();
            super.interrupt();
        }

    }


    /**
     * A multicast receiver which continuously receives heartbeats.
     */
    private final class MulticastReceiverThread extends Thread {

        /**
         * Constructor
         */
        public MulticastReceiverThread() {
            super("Multicast Heartbeat Receiver Thread");
            setDaemon(true);
        }


        public final void run() {
            byte[] buf = new byte[1000];
            try {
                while (!stopped) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    try {
                        socket.receive(packet);
                        processPacket(packet);

                    } catch (IOException e) {
                        if (!stopped) {
                            log.info("Error receiving heartbeat. " + e.getMessage() + ". Initial cause was " + e.getMessage(), e);
                        }
                    }
                }
            } catch (Throwable t) {
                log.info("Multicast receiver thread caught throwable. Cause was " + t.getMessage() + ". Continuing...");
            }
        }


        /*
         * updates the hosts timestamps
         */
        private void processPacket(DatagramPacket packet) {
            hostsTimestamp.put(packet.getAddress().toString().substring(1), Long.valueOf(System.currentTimeMillis()));
        }


        /*
         * 
         */
        public final void interrupt() {
            try {
                socket.leaveGroup(groupMulticastAddress);
            } catch (IOException e) {
                log.info("Error leaving group");
            }
            socket.close();
            super.interrupt();
        }
    }

}
