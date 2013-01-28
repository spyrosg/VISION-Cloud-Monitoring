package gr.ntua.vision.monitoring.rules.propagation.com;

import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.rules.propagation.message.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 */
public class MessageUnicastSender extends Thread implements Observer {

    /***/
    @SuppressWarnings("unused")
    private final static Logger     log     = LoggerFactory.getLogger(MessageUnicastSender.class);
    /***/
    volatile boolean                stopped = false;
    /***/
    private RulesPropagationManager manager;


    /**
     * Shutdown the message receiver.
     */
    public final void halt() {
        stopped = true;
    }


    /**
     * @throws IOException
     */
    public final void init() throws IOException {
        this.start();
    }


    @Override
    public void run() {
        while (!stopped)
            if (!manager.getOutUnicastQueue().isQEmpty())
                sendMessage(manager.getOutQueue().getMessage());
            else
                synchronized (this) {
                    try {
                        wait();
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
    }


    /**
     * @param manager
     */
    public void setManager(final RulesPropagationManager manager) {
        this.manager = manager;
    }


    @Override
    public void update(final Observable o, final Object s) {
        synchronized (this) {
            notify();
        }
    }


    /**
     * @param m
     */
    @SuppressWarnings("static-method")
    private void sendMessage(final Message m) {
        try {
            final Socket soc = new Socket();

            final OutputStream os = soc.getOutputStream();
            final ObjectOutputStream o_os = new ObjectOutputStream(os);
            o_os.writeObject(m);
            os.flush();
            o_os.close();
            os.close();
            soc.close();

        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

}
