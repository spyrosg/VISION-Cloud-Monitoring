package gr.ntua.vision.monitoring.rules.propagation;

import gr.ntua.vision.monitoring.threading.PeriodicTask;

import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class elects the representing host of the cluster 
 * and avoids the transient states of the system by probing 
 * elected host every 5 seconds.
 * 
 * @author tmessini
 */
public class Elector extends PeriodicTask {

    /***/
    final Logger                                   log = LoggerFactory.getLogger(Elector.class);
    /***/
    private RulesPropagationManager manager;
    /***/
    private final long              period = 5000;
    /***/
    private volatile boolean isElected = false;
    /***/
    private volatile Integer previousElectedPid = 0;
    /***/
    private volatile int previousMulticastGroupSize =0;

    

    

    /**
     * Constructor.
     * @param manager 
     */
    public Elector(final RulesPropagationManager manager) {
        this.manager = manager;
    }


    @Override
    public void run() {
        checkNodeRole();
    }

    /**
     * 
     */
    private void checkNodeRole() {
        if (manager.getHeartbeatReceiver().getClusterElectedHost() != null) {            
            
            String elected = manager.getHeartbeatReceiver().getClusterElectedHost();            
            String[] ipPid = elected.split(":");            
            Integer electedPid = Integer.parseInt(ipPid[1]);
                     
            int currentMulticastGroupSize = manager.getHeartbeatReceiver().getMembers().size();
            
            if (electedPid.equals(manager.getPid()) && getPreviousElectedPid().equals(electedPid) && currentMulticastGroupSize == getPreviousMulticastGroupSize()){ 
                setElected(true);                
            } else {
                setElected(false);               
                setPreviousElectedPid(electedPid);                
                setPreviousMulticastGroupSize(currentMulticastGroupSize);
            }
        }
    }


    @Override
    public void scheduleWith(final Timer timer) {
        timer.schedule(this, 0, period);
    }


    /**
     *     
     * @return boolean
     */
    public boolean isElected() {
        return isElected;
    }

    /**
     * 
     * @param isElected
     */
    private void setElected(boolean isElected) {
        this.isElected = isElected;
    }

    /**
     * 
     * @return
     */
    @SuppressWarnings("javadoc")
    private Integer getPreviousElectedPid() {
        return previousElectedPid;
    }

    /**
     * 
     * @param electedPid
     */
    private void setPreviousElectedPid(Integer electedPid) {
        this.previousElectedPid = electedPid;
    }

    
    /**
     * 
     * @return int
     */
    private int getPreviousMulticastGroupSize() {
        return previousMulticastGroupSize;
    }

    /**
     * 
     * @param previousMulticastGroup
     */
    private void setPreviousMulticastGroupSize(int previousMulticastGroup) {
        this.previousMulticastGroupSize = previousMulticastGroup;
    }

}
