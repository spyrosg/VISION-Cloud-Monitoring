package gr.ntua.vision.monitoring.rules.propagation;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.AbstractRuleFactory;
import gr.ntua.vision.monitoring.rules.RuleProc;

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
    private final static Logger      log     = LoggerFactory.getLogger(RulesPropagationManager.class);
    /***/
    private HashMap<Integer, String> content;
    /***/
    private RulesPropagationManager  manager;
    /***/
    private AbstractRuleFactory      factory = new AbstractRuleFactory();


    @Override
    public void run() {
        Message deliveredMessage;
        while (!interrupted())
            if (!manager.getDelQueue().isQEmpty()) {
                deliveredMessage = manager.getDelQueue().getMessage();
                log.info(manager.getPid() + ": message delivered: " + deliveredMessage);
                content = deliveredMessage.getContent();

                String type = deliveredMessage.getType();

                final Iterator<Integer> iterator = content.keySet().iterator();
                while (iterator.hasNext()) {

                    System.out.println(manager.getEngine().getRulesTotalNumber());
                    final String rule = content.get(iterator.next());

                    System.out.println(rule);
                    final String[] ruleParts = rule.split(":");
                    String ruleName = null;
                    String rulePeriod = null;
                    String ruleDesc = null;

                    if (ruleParts != null) {
                        ruleName = ruleParts[0];
                        rulePeriod = ruleParts[1];
                        ruleDesc = ruleParts[2];
                    }

                    RuleProc<Event> ruleObject = factory.createRuleFactory(ruleName).createRule(manager.getEngine(), rulePeriod,
                                                                                                ruleName, ruleDesc);
                    processObject(ruleObject, type);
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
     * 
     */
    public void halt() {
        interrupt();
    }


    /**
     * process the rule Object
     * 
     * @param ruleObject
     * @param type
     */
    private void processObject(RuleProc<Event> ruleObject, String type) {
        if (type.equals("add")) {
            ruleObject.submit();
            System.out.println(manager.getEngine().getRulesTotalNumber());
        }
        if (type.equals("del")) {
            manager.getEngine().removeRule(ruleObject);
            System.out.println(manager.getEngine().getRulesTotalNumber());
        }
    }


    /**
     * @param manager
     */
    public void setManager(final RulesPropagationManager manager) {
        this.manager = manager;
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
