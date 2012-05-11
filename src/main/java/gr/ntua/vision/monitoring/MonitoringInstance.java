package gr.ntua.vision.monitoring;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 *
 */
public class MonitoringInstance implements UDPListener {
    /**
     *
     */
    private static class EventLoop extends MonitoringTask {
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


    /**
     *
     */
    private static class UDPServer extends MonitoringTask {
        /***/
        private final UDPListener    listener;
        /** the log target. */
        private final Logger         log = LoggerFactory.getLogger(getClass());
        /***/
        private final DatagramSocket sock;


        /**
         * @param port
         * @param listener
         * @throws SocketException
         */
        public UDPServer(final int port, final UDPListener listener) throws SocketException {
            super("udp-server");
            this.sock = new DatagramSocket(port);
            this.sock.setReuseAddress(true);
            this.listener = listener;
            log.info("upd server started on port={}", port);
        }


        @Override
        public void run() {
            while (!isInterrupted())
                try {
                    final DatagramPacket pack = receive();
                    final String msg = new String(pack.getData(), 0, pack.getLength());

                    log.debug("received '{}'", msg);

                    send(listener.notify(msg), pack.getAddress(), pack.getPort());
                } catch (final IOException e) {
                    log.error("while receiving", e);
                }
        }


        /**
         * @see gr.ntua.vision.monitoring.MonitoringTask#shutDown()
         */
        @Override
        void shutDown() {
            interrupt();
            sock.close();
        }


        /**
         * Receive a datagram packet from the socket.
         * 
         * @return a {@link DatagramPacket}.
         * @throws IOException
         */
        private DatagramPacket receive() throws IOException {
            final byte[] buf = new byte[64];
            final DatagramPacket req = new DatagramPacket(buf, buf.length);

            sock.receive(req);

            return req;
        }


        /**
         * Send as a datagram packet the given content.
         * 
         * @param payload
         *            the payload to send.
         * @param addr
         *            the address to sent to.
         * @param port
         *            the port to sent to.
         * @throws IOException
         */
        private void send(final String payload, final InetAddress addr, final int port) throws IOException {
            final byte[] buf = payload.getBytes();
            final DatagramPacket res = new DatagramPacket(buf, buf.length, addr, port);

            sock.send(res);
        }
    }

    /***/
    private static final String        KILL            = "stop!";
    /***/
    private static final String        STATUS          = "status?";
    /** the zmq context. */
    private final ZContext             ctx             = new ZContext();
    /** the log target. */
    private final Logger               log             = LoggerFactory.getLogger(getClass());
    /***/
    private final List<MonitoringTask> tasks           = new ArrayList<MonitoringTask>();
    /** the udp port. */
    private final int                  UDP_SERVER_PORT = 56431;


    /**
     * Constructor.
     */
    public MonitoringInstance() {
        log.info("Starting up, pid={}, ip={}", getVMPID(), getHostNameIP());
        log.info("running zmq version={}", ZMQ.getVersionString());
    }


    /**
     * @see gr.ntua.vision.monitoring.UDPListener#notify(java.lang.String)
     */
    @Override
    public String notify(final String msg) {
        if (msg.equals(STATUS))
            return status();

        stop();
        return KILL;
    }


    /**
     * Actually start the application. Setup and run any supporting tasks.
     * 
     * @throws SocketException
     */
    public void start() throws SocketException {
        startService(new UDPServer(UDP_SERVER_PORT, this));
        joinCluster();
        startService(new EventLoop(ctx));
    }


    /**
     * Stop the application. Wait for the supporting tasks to stop.
     */
    public void stop() {
        log.info("shutting down");
    }


    /**
     * 
     */
    private void joinCluster() {
        final Socket s = ctx.createSocket(ZMQ.REQ);

        s.connect("ipc://join");
        s.send("new-machine:ip".getBytes(), 0);
        s.recv(0);
        log.info("joined cluster");
    }


    /**
     * Start running the task asynchronously.
     * 
     * @param task
     *            the task to run.
     */
    private void startService(final MonitoringTask task) {
        tasks.add(task);
        task.setDaemon(true);
        task.start();
    }


    /**
     * @return
     */
    @SuppressWarnings("static-method")
    private String status() {
        return String.valueOf(getVMPID());
    }


    /**
     * @return
     */
    private static String getHostNameIP() {
        return "FIXME";
    }


    /**
     * @return the pid of the running jvm.
     * @throws Error
     *             when the pid is not available for this jvm.
     */
    private static int getVMPID() {
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf("@");

        if (index < 0)
            throw new Error("Cannot get the pid of this jvm");

        return Integer.parseInt(jvmName.substring(0, index));
    }
}
