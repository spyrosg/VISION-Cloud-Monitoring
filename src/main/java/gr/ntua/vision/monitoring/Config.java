package gr.ntua.vision.monitoring;

import java.lang.management.ManagementFactory;


/**
 *
 */
public class Config {
    /***/
    private static final int    COMMAND_SERVER_PORT = 56431;
    /***/
    private static final String KILL_COMMAND        = "kill!";
    /***/
    private static final int    pid                 = getVMPID();
    /***/
    private static final int    RESPONSE_TIMEOUT    = 1000;
    /***/
    private static final String STATUS_COMMAND      = "pid?";


    /**
     * @return the kill command string.
     */
    public String getKillCommand() {
        return KILL_COMMAND;
    }


    /**
     * @return the pid of the vm.
     */
    public int getPID() {
        return pid;
    }


    /**
     * @return the command server port.
     */
    public int getPort() {
        return COMMAND_SERVER_PORT;
    }


    /**
     * @return the status command string.
     */
    public String getStatusCommand() {
        return STATUS_COMMAND;
    }


    /**
     * @return the command server timeout.
     */
    public int getTimeout() {
        return RESPONSE_TIMEOUT;
    }


    /**
     * @return this vm's pid.
     */
    private static int getVMPID() {
        // NOTE: expecting something like '<pid>@<hostname>'
        final String vmname = ManagementFactory.getRuntimeMXBean().getName();
        final int atIndex = vmname.indexOf( "@" );

        if( atIndex < 0 )
            throw new Error( "Cannot get pid: pid N/A for this jvm." );

        return Integer.valueOf( vmname.substring( 0, atIndex ) );
    }
}
