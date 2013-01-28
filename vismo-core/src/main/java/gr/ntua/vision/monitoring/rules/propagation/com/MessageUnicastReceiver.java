package gr.ntua.vision.monitoring.rules.propagation.com;

import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.rules.propagation.message.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Basic Message Unicast Receiver Thread
 * @author tmessini
 */
public class MessageUnicastReceiver extends Thread {

    /***/
    private final static Logger     log        = LoggerFactory.getLogger(MessageUnicastReceiver.class);
    /***/
    volatile boolean                stopped    = false;
    /***/
    private RulesPropagationManager manager;
    /***/
    private ServerSocket            serverSocket;
    /***/
    private int                     serverPort = 10150;
    /***/
    private Socket                  clientSocket;


    /**
     * Shutdown the message receiver.
     * @throws IOException 
     */
    public final void halt() throws IOException {
        stopped = true;
        serverSocket.close();
    }


    /**
     * @throws IOException
     */
    public final void init() throws IOException {
        serverSocket = new ServerSocket(serverPort);
        this.start();

    }


    @Override
    public final void run() {

        while (!stopped)
            try {
                clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(500);
                InputStream in = clientSocket.getInputStream();
                final ObjectInputStream o_in = new ObjectInputStream(in);
                final Message msg = (Message) o_in.readObject();
                manager.getInQueue().addMessage(msg);
                in.close();
                o_in.close();
            } catch (final Exception e) {
                if (!stopped)
                    log.debug("Error receiving message. " + e.getMessage() + ". Initial cause was " + e.getMessage(), e);
            }
    }


    /**
     * @param manager
     */
    public void setManager(final RulesPropagationManager manager) {
        this.manager = manager;
    }

}
