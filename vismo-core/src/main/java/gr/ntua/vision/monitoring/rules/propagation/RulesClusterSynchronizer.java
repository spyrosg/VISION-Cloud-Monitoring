package gr.ntua.vision.monitoring.rules.propagation;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 */
public class RulesClusterSynchronizer extends Thread implements Observer {

    /***/
    private final static Logger     log = LoggerFactory.getLogger(RulesClusterSynchronizer.class);
    /***/
    private RulesPropagationManager manager;
    /***/
    private ConcurrentHashMap<String, Long> rulesSet           = new ConcurrentHashMap<String, Long>();


    /**
     * 
     */
    public void halt() {
        interrupt();
    }


    /**
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        while (!interrupted())
            if (!manager.getRulesResolutionQueue().isQEmpty()) {
                Message incomingMessage = manager.getRulesResolutionQueue().getMessage();
                //log.info(incomingMessage.toString());
                if (!rulesSet.containsKey(incomingMessage.getCommand()))
                rulesSet.put(incomingMessage.getCommand(), incomingMessage.getUpdateDiff());
                else
                {
                    long diff = rulesSet.get(incomingMessage.getCommand());
                    
                    if(incomingMessage.getUpdateDiff() < diff)
                    {
                        rulesSet.put(incomingMessage.getCommand(), incomingMessage.getUpdateDiff());  
                    }
                }
                log.info("ruleSet"+rulesSet.toString());
            } else
                synchronized (this) {
                    try {
                        wait();
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                }
    }


    /**
     * @param manager
     */
    public void setManager(final RulesPropagationManager manager) {
        this.manager = manager;
    }
    
    /**
     * 
     */
    public void clearRuleSet(){
        rulesSet.clear();
    }


    /**
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(final Observable o, final Object m) {
        synchronized (this) {
            notify();
        }
    }
    
}
