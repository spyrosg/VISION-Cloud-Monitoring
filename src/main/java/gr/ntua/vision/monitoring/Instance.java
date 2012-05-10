package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.lifecycle.Supervisor;

import java.net.SocketException;


/**
 *
 */
public class Instance implements Supervisor {
    /***/
    private final CommandServer commandServer;
    /***/
    private final Thread        t;


    /**
     * Constructor.
     * 
     * @param cnf
     *            the configuration object.
     * @param fixme
     * @throws SocketException
     */
    public Instance(final Config cnf, final Object fixme) throws SocketException {
        this.commandServer = new CommandServer(cnf, this);
        this.t = new Thread(this.commandServer, "command-server");
        // FIXME: this.t.setDaemon(true);
    }


    /**
     * @see gr.ntua.vision.monitoring.lifecycle.Supervisor#start()
     */
    @Override
    public void start() {
        t.start();
    }


    /**
     * @see gr.ntua.vision.monitoring.lifecycle.Supervisor#stop()
     */
    @Override
    public void stop() {
        t.interrupt();
        commandServer.closeConnection();
    }
}
