package gr.ntua.vision.monitoring.rules.propagation.com;

import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.rules.propagation.message.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 */
public class MessageMulticastSender extends Thread implements Observer {

    /***/
    private final static Logger     log                   = LoggerFactory.getLogger(MessageMulticastSender.class);
    /***/
    volatile boolean                stopped               = false;
    /***/
    private final String            groupMulticastAddress = "224.0.0.1";
    /***/
    private final Integer           groupMulticastPort    = 6308;
    /***/
    private RulesPropagationManager manager;
    /***/
    private final int               retries               = 3;
    /***/
    private MulticastSocket         socket;
    /***/
    private final int               timeToLive            = 1;


    /**
     * @return multicast address.
     * @throws UnknownHostException
     */
    public InetAddress getGroupMulticastAddress() throws UnknownHostException {
        return InetAddress.getByName(groupMulticastAddress);
    }


    /**
     * @return the TTL
     */
    public int getTimeToLive() {
        return timeToLive;
    }


    /**
     * Shutdown the message receiver.
     */
    public void halt() {
        stopped = true;
        socket.close();
        interrupt();
    }


    /**
     * @throws IOException
     */
    public void init() throws IOException {
        socket = new MulticastSocket(groupMulticastPort.intValue());
        socket.joinGroup(InetAddress.getByName(groupMulticastAddress));
        this.start();
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        while (!stopped)
            if (!manager.getOutQueue().isQEmpty()) {
                if (!BMulticastMessage(manager.getOutQueue().getMessage()))
                    MessageMulticastSender.log.info("ID#" + manager.getPid() + "\tMULTICAST\t" + "FAILED");
            } else
                synchronized (this) {
                    try {
                        wait();
                    } catch (final InterruptedException e) {
                        //TODO
                    }
                }
    }


    /**
     * @param manager
     */
    public void setManager(final RulesPropagationManager manager) {
        this.manager = manager;
    }


    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(final Observable o, final Object s) {
        synchronized (this) {
            notify();
        }
    }


    /**
     * @param m
     * @return true or false
     */
    private boolean BMulticastMessage(final Message m) {
        boolean multicast_message_sent = true;
        multicast_message_sent = multicast_message_sent & sendMessage(m);
        return multicast_message_sent;
    }


    /**
     * @param m
     * @return true or false
     */
    private boolean sendMessage(final Message m) {
        for (int i = 0; i < retries; i++)
            try {
                socket = new MulticastSocket(groupMulticastPort.intValue());
                socket.setTimeToLive(getTimeToLive());
                socket.joinGroup(getGroupMulticastAddress());
                final ByteArrayOutputStream ba_os = new ByteArrayOutputStream();
                final ObjectOutputStream o_os = new ObjectOutputStream(ba_os);
                o_os.writeObject(m);
                final byte[] b = ba_os.toByteArray();
                final DatagramPacket datagram = new DatagramPacket(b, b.length, getGroupMulticastAddress(),
                        groupMulticastPort.intValue());
                socket.send(datagram);
                socket.close();
                return true;
            } catch (final Exception e) {
                e.printStackTrace();
            }
        return false;
    }
}
