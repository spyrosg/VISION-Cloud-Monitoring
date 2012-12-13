package gr.ntua.vision.monitoring.heartbeat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Sends heartbeats to a multicast group.
 * <p/>
 * You can control how far the multicast packets propagate by setting the TTL value indicates the scope or range in which a packet
 * may be forwarded. By convention:
 * <ul>
 * <li>0 is restricted to the same host
 * <li>1 is restricted to the same subnet
 * <li>32 is restricted to the same site
 * <li>64 is restricted to the same region
 * <li>255 is unrestricted
 * </ul>
 * You can also control how often the heartbeat sends by setting the interval.
 */
public final class HeartbeatSender {

    /**
     * A thread which sends a multicast heartbeat
     */
    private final class MulticastSenderThread extends Thread {
        /**
         * 
         */
        private MulticastSocket socket;


        /**
         * 
         */
        public MulticastSenderThread() {
            // TODO Auto-generated constructor stub
        }

        /*
         * @see java.lang.Thread#interrupt()
         */
        @Override
        public final void interrupt() {
            closeSocket();
            super.interrupt();
        }

        /**
         * 
         */
        @Override
        public final void run() {
            while (!stopped) {
                try {
                    socket = new MulticastSocket(groupMulticastPort.intValue());
                    socket.setTimeToLive(getTimeToLive().intValue());
                    socket.joinGroup(getGroupMulticastAddress());

                    final byte[] buffer = createPayload();
                    final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, getGroupMulticastAddress(),
                            groupMulticastPort.intValue());
                    log.info("MSender" + SENDER_ID + ": sending packet to: "
                            + packet.getSocketAddress().toString().substring(1));
                    socket.send(packet);
                } catch (final IOException e) {
                    log.debug("Error on multicast socket", e);
                } catch (final Throwable e) {
                    log.debug("Unexpected throwable in run thread. Continuing..." + e.getMessage(), e);
                } finally {
                    closeSocket();
                }
                if (!stopped)
                    try {
                        Thread.sleep(getSendInterval());
                    } catch (final InterruptedException e) {
                        log.debug("Sleep after error interrupted. Initial cause was " + e.getMessage());
                    }
            }
        }



        /**
         *    
         */
        private void closeSocket() {
            try {
                if (socket != null && !socket.isClosed()) {
                    try {
                        socket.leaveGroup(getGroupMulticastAddress());
                    } catch (final IOException e) {
                        HeartbeatSender.log.debug("Error leaving multicast group. Message was " + e.getMessage());
                    }
                    socket.close();
                }
            } catch (final NoSuchMethodError e) {
                HeartbeatSender.log.debug("socket.isClosed is not supported by JDK");
                try {
                    socket.leaveGroup(getGroupMulticastAddress());
                } catch (final IOException ex) {
                    HeartbeatSender.log.debug("Error leaving multicast group. Message was " + ex.getMessage());
                }
                socket.close();
            }
        }


        /**
         * create the multicast packet payload containing the senderID
         * @return 
         * 
         * @throws SocketException
         * @throws UnknownHostException
         */
        @SuppressWarnings("javadoc")
        private byte[] createPayload() throws UnknownHostException, SocketException {
            final byte[] msg = (SENDER_ID + "").getBytes();
            return msg;
        }

    }

    /**
     * 
     */
    private final int      DEFAULT_HEARTBEAT_INTERVAL = 1000;
    /**
     * 
     */
    private long           sendInterval          = DEFAULT_HEARTBEAT_INTERVAL;
    /**
     * 
     */
    static final Logger   log                        = LoggerFactory.getLogger(HeartbeatSender.class);
    /**
     * 
     */
    private final int      MINIMUM_HEARTBEAT_INTERVAL = 500;
    /**
     * 
     */
    int                   SENDER_ID;
    /**
     * 
     */
    private final InetAddress     groupMulticastAddress;
    /**
     * 
     */
    final Integer         groupMulticastPort;
    /**
     * 
     */
    private MulticastSenderThread senderThread;
    /**
     * 
     */
    boolean               stopped;

    /**
     * 
     */
    private final Integer         timeToLive;


    /**
     * Constructor.
     * 
     * @param multicastAddress
     * @param multicastPort
     * @param timeToLive
     *            See class description for the meaning of this parameter.
     */
    public HeartbeatSender(final InetAddress multicastAddress, final Integer multicastPort, final Integer timeToLive) {
        System.setProperty("Djava.net.preferIPv4Stack", "true");
        this.groupMulticastAddress = multicastAddress;
        this.groupMulticastPort = multicastPort;
        this.timeToLive = timeToLive;
        this.SENDER_ID = getRandomID();
    }


    /**
     * Returns the heartbeat interval.
     * @return interval
     */
    public long getHeartBeatInterval() {
        return getSendInterval();
    }


    /**
     * @return the TTL
     */
    public Integer getTimeToLive() {
        return timeToLive;
    }


    /**
     * Shutdown this heartbeat sender
     */
    public final void halt() {
        stopped = true;
        senderThread.interrupt();
    }


    /**
     * Start the heartbeat thread
     */
    public final void init() {
        senderThread = new MulticastSenderThread();
        senderThread.start();
    }


    /**
     * Sets the heartbeat interval to something other than the default of 2000ms.
     * 
     * @param heartBeatInterval
     *            a time in ms, greater than 1000
     */
    public void setHeartBeatInterval(final long heartBeatInterval) {
        if (heartBeatInterval < MINIMUM_HEARTBEAT_INTERVAL) {
            HeartbeatSender.log.info("Trying to set heartbeat interval too low. Using MINIMUM_HEARTBEAT_INTERVAL instead.");
            setSendInterval(MINIMUM_HEARTBEAT_INTERVAL);
        } else
            setSendInterval(heartBeatInterval);
    }


    /**
     * 
     * @return a random id for process
     */
    private static int getRandomID() {
        final Random randomGenerator = new Random();
        return randomGenerator.nextInt(10000);
    }


    /**
     * @return multicast address.
     */
    public InetAddress getGroupMulticastAddress() {
        return groupMulticastAddress;
    }


    /**
     * @return the heartbeat sender interval
     */
    public long getSendInterval() {
        return sendInterval;
    }


    /**
     * @param sendInterval
     */
    public void setSendInterval(long sendInterval) {
        this.sendInterval = sendInterval;
    }
}
