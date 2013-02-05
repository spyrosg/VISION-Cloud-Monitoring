package gr.ntua.vision.monitoring.rules.propagation.services;

import gr.ntua.vision.monitoring.rules.propagation.RulesPropagationManager;
import gr.ntua.vision.monitoring.threading.PeriodicTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class elects the representing host of the cluster and avoids the transient states of the system by probing elected host
 * every 5 seconds.
 * 
 * @author tmessini
 */
public class Elector extends PeriodicTask {
    /***/
    private static final Logger           log                        = LoggerFactory.getLogger(Elector.class);
    /***/
    private static final long             period                     = 5000;
    /***/
    private volatile boolean              isElected                  = false;
    /***/
    private final RulesPropagationManager manager;
    /***/
    private volatile Integer              previousElectedPid         = 0;
    /***/
    private volatile int                  previousMulticastGroupSize = 0;


    /**
     * Constructor.
     * 
     * @param manager
     */
    public Elector(final RulesPropagationManager manager) {
        super(period);
        this.manager = manager;
    }


    /**
     * @return boolean
     */
    public boolean isElected() {
        return isElected;
    }


    /**
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        checkNodeRole();
    }


    /**
     * 
     */
    private void checkNodeRole() {
        if (manager.getHeartbeatReceiver().getClusterElectedHost() != null) {
            final String elected = manager.getHeartbeatReceiver().getClusterElectedHost();

            if (!elected.equals(null)) {
                final String[] ipPid = elected.split(":");

                if (ipPid.length == 2) {
                    final Integer electedPid = Integer.parseInt(ipPid[1]);

                    final int currentMulticastGroupSize = manager.getHeartbeatReceiver().getMembers().size();

                    if (electedPid.equals(manager.getPid()) && getPreviousElectedPid().equals(electedPid)
                            && currentMulticastGroupSize == getPreviousMulticastGroupSize())
                        setElected(true);
                    else {
                        setElected(false);
                        setPreviousElectedPid(electedPid);
                        setPreviousMulticastGroupSize(currentMulticastGroupSize);
                    }
                }
            }
        }
    }


    /**
     * @return
     */
    @SuppressWarnings("javadoc")
    private Integer getPreviousElectedPid() {
        return previousElectedPid;
    }


    /**
     * @return int
     */
    private int getPreviousMulticastGroupSize() {
        return previousMulticastGroupSize;
    }


    /**
     * @param isElected
     */
    private void setElected(final boolean isElected) {
        this.isElected = isElected;
    }


    /**
     * @param electedPid
     */
    private void setPreviousElectedPid(final Integer electedPid) {
        this.previousElectedPid = electedPid;
    }


    /**
     * @param previousMulticastGroup
     */
    private void setPreviousMulticastGroupSize(final int previousMulticastGroup) {
        this.previousMulticastGroupSize = previousMulticastGroup;
    }

}
