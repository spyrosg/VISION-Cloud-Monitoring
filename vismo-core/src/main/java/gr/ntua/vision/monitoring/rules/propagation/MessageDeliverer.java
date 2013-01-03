package gr.ntua.vision.monitoring.rules.propagation;

import gr.ntua.vision.monitoring.rules.PassThroughRule;
import gr.ntua.vision.monitoring.rules.TestingRule;
import gr.ntua.vision.monitoring.rules.AccountingRule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author tmessini
 */
public class MessageDeliverer extends Thread implements Observer {

    /***/
    private final static Logger      log = LoggerFactory.getLogger(RulesPropagationManager.class);
    /***/
    private HashMap<Integer, String> content;
    /***/
    private RulesPropagationManager  manager;


    @Override
    public void run() {
        Message deliveredMessage;
        while (true)
            if (!manager.getDelQueue().isQEmpty()) {
                // TODO do the pretty thing!
                deliveredMessage = manager.getDelQueue().getMessage();
                //log.info(manager.getPid() + ": message delivered: " + deliveredMessage);
                content = deliveredMessage.getContent();

                final Iterator<Integer> iterator = content.keySet().iterator();
                // check all the values in content
                while (iterator.hasNext()) {
                    final String rule = content.get(iterator.next());
                    final String[] ruleParts = rule.split(":");
                    
                    //TestingRule
                    if (ruleParts[0].equals("TestingRule")) {
                        final TestingRule testingRule = new TestingRule(manager.getEngine(), ruleParts[0], ruleParts[2]);
                        testingRule.submitTo(manager.getEngine());
                        log.info(manager.getPid() + ": rule: " + rule + " added! engineSize: " +manager.getEngine().getRulesTotalNumber());
                    }
                    
                    //PassThroughRule
                    if (ruleParts[0].equals("PassThroughRule")) {
                        final PassThroughRule passThroughRule = new PassThroughRule(manager.getEngine());                      
                        passThroughRule.submitTo(manager.getEngine());
                        manager.getEngine().removeRule(passThroughRule);
                        log.info(manager.getPid() + ": rule: " + rule + " added! engineSize: " +manager.getEngine().getRulesTotalNumber());
                    }
                    
                    //AccountingRule
                    if (ruleParts[0].equals("AccountingRule")) {
                        final AccountingRule aggregationRule = new AccountingRule(manager.getEngine(), Long.valueOf(ruleParts[1]));
                        aggregationRule.submitTo(manager.getEngine());
                        log.info(manager.getPid() + ": rule: " + rule + " added! engineSize: " +manager.getEngine().getRulesTotalNumber());
                    }

                }

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


    @Override
    public void update(final Observable o, final Object m) {
        synchronized (this) {
            notify();
        }
    }
}
