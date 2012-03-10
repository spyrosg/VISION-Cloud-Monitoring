package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.lifecycle.Supervisor;

import java.net.SocketException;


/**
 *
 */
public class InstanceManager implements Supervisor {
    /** the configuration object. */
    private final Config            cnf;
    /***/
    private final CommandServer     commandServer;
    /***/
    private final MonitoringService mon;


    /**
     * Constructor.
     * 
     * @param cnf
     *            the configuration object.
     * @param mon
     * @throws SocketException
     */
    public InstanceManager(final Config cnf, final MonitoringService mon) throws SocketException {
        this.cnf = cnf;
        this.mon = mon;
        this.commandServer = new CommandServer( cnf, this );
    }


    /**
     * Start the manager.
     */
    @Override
    public void start() {
        mon.start();
        startCommandServer();
    }


    /**
     * 
     */
    void stop() {
        mon.stop();
        commandServer.closeConnection();
    }


    /**
     * 
     */
    private void startCommandServer() {
        final Thread t = new Thread( commandServer, "udp-command-server" );
        t.setDaemon( true );
        t.start();
    }
}
