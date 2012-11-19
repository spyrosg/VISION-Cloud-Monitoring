package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;


/**
 *
 */
public interface Rule {
    /**
     * This is the <strong>when</strong> part of the rule. If this is <code>true</code> the rule is activated, i.e, will be
     * consequently run.
     * 
     * @param e
     *            an event to match against.
     * @return <code>true</code> if the rule applies to the event, <code>false</code> otherwise.
     */
    boolean matches(Event e);
}
