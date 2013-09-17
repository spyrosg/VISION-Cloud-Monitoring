package endtoend.tests;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 *
 */
public class PerRuleTopicEventHandler extends NoEventsCheckingHandler {
    /***/
    private final String topic;


    /**
     * Constructor.
     * 
     * @param topic
     */
    public PerRuleTopicEventHandler(final String topic) {
        this.topic = topic;
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void handle(final MonitoringEvent e) {
        if (!topic.equals(e.topic()))
            return;

        receivedEvent(e);
    }
}
