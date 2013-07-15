package gr.ntua.vision.monitoring.queues;

import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.util.Collections;
import java.util.List;


/**
 *
 */
public class TopicedQueueListBean extends TopicedQueueBean {
    /***/
    private List<MonitoringEvent> value;


    /**
     * Constructor.
     */
    public TopicedQueueListBean() {
        super();
        this.value = Collections.emptyList();
    }


    /**
     * Constructor.
     * 
     * @param objectName
     */
    public TopicedQueueListBean(final String objectName) {
        super(objectName);
        this.value = Collections.emptyList();
    }


    /**
     * @return the value
     */
    public List<MonitoringEvent> getValue() {
        return value;
    }


    /**
     * @param value
     *            the value to set
     */
    public void setValue(final List<MonitoringEvent> value) {
        this.value = value;
    }
}
