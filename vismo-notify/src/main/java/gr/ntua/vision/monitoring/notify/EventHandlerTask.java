package gr.ntua.vision.monitoring.notify;

import gr.ntua.monitoring.sockets.Socket;
import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import java.util.logging.Logger;


/**
 * The event handler task is responsible for listening for incoming events and pass those events to the handler.
 */
public class EventHandlerTask implements Runnable {
    /** the log target. */
    private static final Logger ilog = Logger.getLogger(EventHandlerTask.class.getName());
    /** the event factory. */
    private final EventFactory  factory;
    /** the actual handler. */
    private final EventHandler  handler;
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
        this.handler = handler;
    }


    /**
     * Halt the task's execution.
     */
    @SuppressWarnings("static-method")
    public void halt() {
        Thread.currentThread().interrupt();
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        ilog.config("entering receive/handle loop");

        while (!Thread.currentThread().isInterrupted()) {
            final String msg = sock.receive();

            ilog.fine("received: " + msg);

            if (msg == null)
                continue;

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

        sock.close();
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<EventHandlerTask: " + sock + ">";
    }
}
