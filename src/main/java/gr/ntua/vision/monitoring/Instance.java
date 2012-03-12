package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.lifecycle.Supervisor;

import java.net.SocketException;


/**
 *
 */
public class Instance implements Supervisor {
    /** the configuration object. */
    private final Config        cnf;
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
        this.cnf = cnf;
        this.commandServer = new CommandServer( cnf, this );
        this.t = new Thread( this.commandServer, "command-server" );
        this.t.setDaemon( true );
    }


    /**
     * 
     */
    @Override
    public void start() {
        t.start();
    }


    /**
     * 
     */
    @Override
    public void stop() {
        t.interrupt();
        commandServer.closeConnection();
    }
}
