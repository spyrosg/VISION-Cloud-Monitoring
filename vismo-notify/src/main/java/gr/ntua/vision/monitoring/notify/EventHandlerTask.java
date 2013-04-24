package gr.ntua.vision.monitoring.notify;

import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.sockets.Socket;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.util.logging.Logger;


/**
 * The event handler task is responsible for listening for incoming events and pass those events to the handler.
 */
public class EventHandlerTask implements Runnable {
    /** the log target. */
    private static final Logger ilog    = Logger.getLogger(EventHandlerTask.class.getName());
    /** the event factory. */
    private final EventFactory  factory;
    /** the actual handler. */
    private final EventHandler  handler;
    /***/
    private volatile boolean    running = false;
    /***/
    private final String        shutdownCommand;
    /***/
    private final Socket        shutdownSock;
    /** the socket. */
    private final Socket        sock;


    /**
     * Constructor.
     * 
     * @param factory
     *            the event factory.
     * @param socketFactory
     * @param addr
     * @param topic
     * @param handler
     *            the actual handler.
     */
    EventHandlerTask(final EventFactory factory, final ZMQFactory socketFactory, final String addr, final String topic,
            final EventHandler handler) {
        this.factory = factory;
        this.sock = socketFactory.newSubSocket(addr, topic);
        this.shutdownSock = socketFactory.newPubConnectSocket(addr);
        this.handler = handler;
        this.shutdownCommand = topic + " shutdown";
    }


    /**
     * Halt the task's execution.
     */
    public void halt() {
        shutdownSock.send(shutdownCommand);
    }


    /**
     * @return <code>true</code> iff the runnable is still in the receive loop, <code>false</code> otherwise.
     */
    public boolean isRunning() {
        return running;
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        running = true;
        ilog.config("entering receive/handle loop");

        while (!Thread.currentThread().isInterrupted()) {
            final String msg = sock.receive();

            ilog.fine("received: " + msg);

            if (msg == null)
                continue;
            if (shutdownCommand.equals(msg))
                break;

            // bypass topic
            final int topicIndex = msg.indexOf(" ");
            final MonitoringEvent e = factory.createEvent(msg.substring(topicIndex + 1));

            if (e != null)
                try {
                    handler.handle(e);
                } catch (final Throwable x) {
                    x.printStackTrace();
                }
        }

        ilog.config("leaving loop");

        sock.close();
        running = false;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<EventHandlerTask: " + sock + ">";
    }
}
