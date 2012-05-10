package gr.ntua.vision.monitoring;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 *
 */
public class MonitoringInstance {
    /**
     *
     */
    private static class EventLoop implements Runnable {
        /***/
        private final Socket s;


        /**
         * @param ctx
         */
        public EventLoop(final ZContext ctx) {
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
         * @param message
         */
        private void send(final String message) {
            s.send(message.getBytes(), 0);
        }
    }


    /**
     *
     */
    private static class UDPServer implements Runnable {
        /** the log target. */
        private final Logger         log = LoggerFactory.getLogger(getClass());
        /***/
        private final DatagramSocket sock;


        /**
         * @param port
         * @throws SocketException
         */
        public UDPServer(final int port) throws SocketException {
            this.sock = new DatagramSocket(port);
            this.sock.setReuseAddress(true);
            log.info("upd server started on port={}", port);
        }


        @Override
        public void run() {
            try {
                final DatagramPacket pack = receive();

                send("1234", pack.getAddress(), pack.getPort());
            } catch (final SocketException e) {
                e.printStackTrace();
            } catch (final IOException e) {
                e.printStackTrace();
            }
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
    private final ZContext        ctx             = new ZContext();

    /***/
    private final ExecutorService executor        = Executors
                                                          .newFixedThreadPool(/*2 * Runtime.getRuntime().availableProcessors() +*/1);
    /** the log target. */
    private final Logger          log             = LoggerFactory.getLogger(getClass());
    /***/
    private final int             UDP_SERVER_PORT = 56431;


    /**
     * Constructor.
     */
    public MonitoringInstance() {
        log.info("Starting up, pid={}, ip={}", getVMPID(), "FIXME");
        log.info("running zmq version={}", ZMQ.getVersionString());
    }


    /**
     * @throws SocketException
     */
    public void start() throws SocketException {
        executor.submit(new UDPServer(UDP_SERVER_PORT));
        joinCluster();
        log.info("joined cluster");
        executor.submit(new EventLoop(ctx));
    }


    /**
     * 
     */
    private void joinCluster() {
        final Socket s = ctx.createSocket(ZMQ.REQ);

        s.connect("ipc://join");
        s.send("new-machine:ip".getBytes(), 0);
        s.recv(0);
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
