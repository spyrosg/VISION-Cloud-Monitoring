package gr.ntua.vision.monitoring.rules.propagation.com;

import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.rules.propagation.message.Message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 */
public class MessageMulticastReceiver extends Thread {

    /***/
    private final static Logger     log                   = LoggerFactory.getLogger(MessageMulticastReceiver.class);
    /***/
    volatile boolean                stopped               = false;
    /***/
    private final String            groupMulticastAddress = "224.0.0.1";
    /***/
    private final Integer           groupMulticastPort    = 6308;
    /***/
    private RulesPropagationManager manager;
    /***/
    private MulticastSocket         socket;


    /**
     * Shutdown the message receiver.
     */
    public final void halt() {
        stopped = true;
        socket.close();
    }


    /**
     * @throws IOException
     */
    public final void init() throws IOException {
        socket = new MulticastSocket(groupMulticastPort.intValue());
        socket.joinGroup(InetAddress.getByName(groupMulticastAddress));
        this.start();

    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public final void run() {
        try {
            while (!stopped)
                try {
                    final byte[] b = new byte[65536];
                    final ByteArrayInputStream ba_is = new ByteArrayInputStream(b);
                    final DatagramPacket datagram = new DatagramPacket(b, b.length);
                    socket.receive(datagram);
                    final ObjectInputStream o_in = new ObjectInputStream(ba_is);
                    final Message msg = (Message) o_in.readObject();
                    manager.getDelQueue().addMessage(msg);
                    ba_is.close();
                    o_in.close();

                } catch (final IOException e) {
                    if (!stopped)
                        MessageMulticastReceiver.log.debug("Error receiving message. " + e.getMessage() + ". Initial cause was "
                                + e.getMessage(), e);
                }
        } catch (final Throwable t) {
            MessageMulticastReceiver.log.debug("Message receiver thread caught throwable. Cause was " + t.getMessage()
                    + ". Continuing...");
        }
    }


    /**
     * @param manager
     */
    public void setManager(final RulesPropagationManager manager) {
        this.manager = manager;
    }

}
