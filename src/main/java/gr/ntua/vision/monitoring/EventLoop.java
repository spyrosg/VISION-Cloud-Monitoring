package gr.ntua.vision.monitoring;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 *
 */
class EventLoop extends MonitoringTask {
    /***/
    private final Socket s;


    /**
     * @param ctx
     */
    public EventLoop(final ZContext ctx) {
        super("event-loop");
        this.s = ctx.createSocket(ZMQ.REQ);
    }


    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        s.connect("ipc://events");

        for (int i = 0; i < 10; ++i) {
            send("foo");
            s.recv(0);
        }

        s.close();
    }


    /**
     * @see gr.ntua.vision.monitoring.MonitoringTask#shutDown()
     */
    @Override
    void shutDown() {
    }


    /**
     * @param message
     */
    private void send(final String message) {
        s.send(message.getBytes(), 0);
    }
}
