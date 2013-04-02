package gr.ntua.vision.monitoring.heartbeat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
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
        /***/
        private MulticastSocket socket;


        /**
         * constructor
         */
        public MulticastSenderThread() {

        }


        /**
         * @see java.lang.Thread#interrupt()
         */
        @Override
        public final void interrupt() {
            closeSocket();
            super.interrupt();
        }


        /**
         * @see java.lang.Thread#run()
         */
        @Override
        public final void run() {
            while (!stopped) {
                try {
                    socket = new MulticastSocket(groupMulticastPort.intValue());
                    socket.setTimeToLive(getTimeToLive());
                    socket.joinGroup(getGroupMulticastAddress());

                    final byte[] buffer = createPayload();
                    final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, getGroupMulticastAddress(),
                            groupMulticastPort.intValue());
                    socket.send(packet);
                } catch (final IOException e) {
                    HeartbeatSender.log.debug("Error on multicast socket", e);
                } catch (final Throwable e) {
                    HeartbeatSender.log.debug("Unexpected throwable in run thread. Continuing..." + e.getMessage(), e);
                } finally {
                    closeSocket();
                }
                if (!stopped)
                    try {
                        Thread.sleep(getSendInterval());
                    } catch (final InterruptedException e) {
                        // TODO removing messages
                    }
            }
        }


        /**
         * closes the socket.
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
         * 
         * @return payload.
         */
        private byte[] createPayload() {
            return Integer.toString(SENDER_ID).getBytes();
        }

    }

    /***/
    static final Logger           log                        = LoggerFactory.getLogger(HeartbeatSender.class);
    /***/
    final Integer                 groupMulticastPort;
    /***/
    Integer                       SENDER_ID;
    /***/
    boolean                       stopped;
    /***/
    private final int             DEFAULT_HEARTBEAT_INTERVAL = 1000;
    /***/
    private final InetAddress     groupMulticastAddress;
    /***/
    private final int             MINIMUM_HEARTBEAT_INTERVAL = 500;
    /***/
    private MulticastSenderThread senderThread;
    /***/
    private long                  sendInterval               = DEFAULT_HEARTBEAT_INTERVAL;
    /***/
    private final int             timeToLive;


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
     * Constructor.
     * 
     * @param multicastAddress
     * @param multicastPort
     * @param timeToLive
     * @param id
     */
    public HeartbeatSender(final InetAddress multicastAddress, final Integer multicastPort, final Integer timeToLive,
            final Integer id) {
        System.setProperty("Djava.net.preferIPv4Stack", "true");
        this.groupMulticastAddress = multicastAddress;
        this.groupMulticastPort = multicastPort;
        this.timeToLive = timeToLive;
        this.SENDER_ID = id;
    }


    /**
     * @return multicast address.
     */
    public InetAddress getGroupMulticastAddress() {
        return groupMulticastAddress;
    }


    /**
     * Returns the heartbeat interval.
     * 
     * @return interval
     */
    public long getHeartBeatInterval() {
        return getSendInterval();
    }


    /**
     * @return integer
     */
    @SuppressWarnings("static-method")
    public Integer getRandomID() {
        final Random randomGenerator = new Random();
        return Integer.valueOf(randomGenerator.nextInt(100000) + 100000);
    }


    /**
     * @return the heartbeat sender interval
     */
    public long getSendInterval() {
        return sendInterval;
    }


    /**
     * @return the TTL
     */
    public int getTimeToLive() {
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
     * @param sendInterval
     */
    public void setSendInterval(final long sendInterval) {
        this.sendInterval = sendInterval;
    }
}
