package gr.ntua.vision.monitoring.heartbeat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

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

    private static final Logger   log                        = LoggerFactory.getLogger(HeartbeatSender.class);

    private static final int      DEFAULT_HEARTBEAT_INTERVAL = 2000;
    private static final int      MINIMUM_HEARTBEAT_INTERVAL = 1000;

    private static long           heartBeatInterval          = DEFAULT_HEARTBEAT_INTERVAL;

    private final InetAddress     groupMulticastAddress;
    private final Integer         groupMulticastPort;
    private final Integer         timeToLive;
    private MulticastSenderThread senderThread;
    private boolean               stopped;


    /**
     * Constructor.
     * 
     * @param multicastAddress
     * @param multicastPort
     * @param timeToLive
     *            See class description for the meaning of this parameter.
     */
    public HeartbeatSender(InetAddress multicastAddress, Integer multicastPort, Integer timeToLive) {
        System.setProperty("Djava.net.preferIPv4Stack", "true");
        this.groupMulticastAddress = multicastAddress;
        this.groupMulticastPort = multicastPort;
        this.timeToLive = timeToLive;
    }


    /**
     * Start the heartbeat thread
     */
    public final void init() {
        senderThread = new MulticastSenderThread();
        senderThread.start();
    }


    /**
     * Shutdown this heartbeat sender
     */
    public final void dispose() {
        stopped = true;
        senderThread.interrupt();
    }


    /**
     * A thread which sends a multicast heartbeat every second
     */
    private final class MulticastSenderThread extends Thread {
        private MulticastSocket socket;


        public final void run() {
            while (!stopped) {
                try {
                    socket = new MulticastSocket(groupMulticastPort.intValue());
                    socket.setTimeToLive(timeToLive.intValue());
                    socket.joinGroup(groupMulticastAddress);

                    while (!stopped) {
                        byte[] buffer = createPayload();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, groupMulticastAddress,
                                groupMulticastPort.intValue());
                        socket.send(packet);

                        try {
                            wait(heartBeatInterval);

                        } catch (InterruptedException e) {
                            if (!stopped) {
                                log.info("Error receiving heartbeat. Initial cause was " + e.getMessage(), e);
                            }
                        }
                    }
                } catch (IOException e) {
                    log.info("Error on multicast socket", e);
                } catch (Throwable e) {
                    log.info("Unexpected throwable in run thread. Continuing..." + e.getMessage(), e);
                } finally {
                    closeSocket();
                }
                if (!stopped) {
                    try {
                        sleep(heartBeatInterval);
                    } catch (InterruptedException e) {
                        log.info("Sleep after error interrupted. Initial cause was " + e.getMessage(), e);
                    }
                }
            }
        }


        /**
         * create the multicast packet payload.
         * 
         * @throws SocketException
         * @throws UnknownHostException
         */
        private byte[] createPayload() throws UnknownHostException, SocketException {
            String interfaceIp = getHostIP();
            byte[] msg = interfaceIp.getBytes();
            return msg;
        }


        /**
         * gets the ip addresses of the node interfaces.
         * 
         * @return
         * @throws UnknownHostException
         * @throws SocketException
         */
        public String getHostIP() throws UnknownHostException, SocketException {
            String hostAddress = "";
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        hostAddress = buildIPAllInterfacesString(hostAddress, inetAddress);
                    }
                }
            }
            return hostAddress;
        }


        /*
         * 
         */
        private String buildIPAllInterfacesString(String ipString, InetAddress inetAddress) {
            if (ipString.isEmpty())
                ipString = inetAddress.toString().substring(1);
            else
                ipString = ipString + ":" + inetAddress.toString().substring(1);
            return ipString;
        }


        /*
         * @see java.lang.Thread#interrupt()
         */
        public final void interrupt() {
            closeSocket();
            super.interrupt();
        }


        private void closeSocket() {
            try {
                if (socket != null && !socket.isClosed()) {
                    try {
                        socket.leaveGroup(groupMulticastAddress);
                    } catch (IOException e) {
                        log.info("Error leaving multicast group. Message was " + e.getMessage());
                    }
                    socket.close();
                }
            } catch (NoSuchMethodError e) {
                log.info("socket.isClosed is not supported by JDK");
                try {
                    socket.leaveGroup(groupMulticastAddress);
                } catch (IOException ex) {
                    log.info("Error leaving multicast group. Message was " + ex.getMessage());
                }
                socket.close();
            }
        }

    }


    /**
     * Sets the heartbeat interval to something other than the default of 2000ms.
     * 
     * @param heartBeatInterval
     *            a time in ms, greater than 1000
     */
    public static void setHeartBeatInterval(long heartBeatInterval) {
        if (heartBeatInterval < MINIMUM_HEARTBEAT_INTERVAL) {
            log.info("Trying to set heartbeat interval too low. Using MINIMUM_HEARTBEAT_INTERVAL instead.");
            HeartbeatSender.heartBeatInterval = MINIMUM_HEARTBEAT_INTERVAL;
        } else {
            HeartbeatSender.heartBeatInterval = heartBeatInterval;
        }
    }


    /**
     * Returns the heartbeat interval.
     */
    public static long getHeartBeatInterval() {
        return heartBeatInterval;
    }


    /**
     * @return the TTL
     */
    public Integer getTimeToLive() {
        return timeToLive;
    }
}
