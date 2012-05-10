package gr.ntua.vision.monitoring;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;


/**
 * The entry point to the monitoring instance.
 */
public class Main {
    /**
     * List of available commands understood by the server. They match standard UNIX init.d commands.
     */
    private enum Commands {
        /***/
        HELP("help") {
            @Override
            String getHelpString() {
                return "show this help message and exit.";
            }


            @Override
            void run(final Config ignored) throws IOException {
                System.err.println("Usage: java -jar " + PROG + ".jar");
                System.err.println("Options:\n");

                for (final Commands cmd : Commands.values())
                    System.err.println(String.format("  %-10s%-40s", cmd.name().toLowerCase(), cmd.getHelpString()));
            }
        },
        /***/
        START("start") {
            @Override
            String getHelpString() {
                return "start the service.";
            }


            /**
             * Receive a datagram packet from the socket.
             * 
             * @return a {@link DatagramPacket}.
             * @throws IOException
             */
            DatagramPacket receive(final DatagramSocket sock) throws IOException {
                final byte[] buf = new byte[64];
                final DatagramPacket req = new DatagramPacket(buf, buf.length);

                sock.receive(req);

                return req;
            }


            @Override
            void run(final Config cnf) throws IOException {
                final ZContext ctx = new ZContext();
                final Socket s = ctx.createSocket(ZMQ.REQ);

                s.connect("ipc://join");
                send(s, "connected");
                s.recv(0);

                final int UDP_SERVER_PORT = 56431;
                final Thread t = new Thread("udp-server") {
                    @Override
                    public void run() {
                        try {
                            final DatagramSocket sock = new DatagramSocket(UDP_SERVER_PORT);

                            sock.setReuseAddress(true);

                            final DatagramPacket pack = receive(sock);

                            send(sock, "1234", pack.getAddress(), pack.getPort());
                        } catch (final SocketException e) {
                            e.printStackTrace();
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                t.start();
                final Thread eventThread = new Thread("event-loop-thread") {
                    @Override
                    public void run() {
                        final Socket eventSocket = ctx.createSocket(ZMQ.REQ);

                        eventSocket.connect("ipc://events");

                        for (int i = 0; i < 10; ++i) {
                            send(eventSocket, "foo");
                            eventSocket.recv(0);
                        }

                        eventSocket.close();
                    }
                };
                eventThread.start();
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
            void send(final DatagramSocket sock, final String payload, final InetAddress addr, final int port) throws IOException {
                final byte[] buf = payload.getBytes();
                final DatagramPacket res = new DatagramPacket(buf, buf.length, addr, port);

                sock.send(res);
            }


            void send(final Socket sock, final String message) {
                sock.send(message.getBytes(), 0);
            }
        },
        /***/
        STATUS("status") {
            @Override
            String getHelpString() {
                return "print the service status.";
            }


            @Override
            void run(final Config cnf) throws IOException {
            }
        },
        /***/
        STOP("stop") {
            @Override
            String getHelpString() {
                return "stop the service.";
            }


            @Override
            void run(final Config cnf) throws IOException {
            }
        };

        /** the command name. */
        private final String name;


        /**
         * Constructor.
         * 
         * @param name
         *            the command name.
         */
        private Commands(final String name) {
            this.name = name;
        }


        /**
         * @return the corresponding explanatory command message.
         */
        abstract String getHelpString();


        /**
         * Execute the command.
         * 
         * @param cnf
         *            the configuration object.
         * @throws IOException
         */
        abstract void run(final Config cnf) throws IOException;


        /**
         * Check that given string is indeed a valid server command.
         * 
         * @param str
         *            the user specified command.
         * @return <code>true</code> iff the string is a valid server command, <code>false</code> otherwise.
         */
        public static boolean isValidCommand(final String str) {
            for (final Commands cmd : Commands.values())
                if (cmd.name.equals(str))
                    return true;

            return false;
        }
    }

    /** the program name. */
    private static final String PROG = "vismo";


    /**
     * @param args
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {
        if (args.length == 0 || !Commands.isValidCommand(args[0])) {
            Commands.HELP.run(null);
            return;
        }

        final Config cnf = new Config();
        final Commands cmd = Commands.valueOf(args[0].toUpperCase());

        cmd.run(cnf);
    }
}
