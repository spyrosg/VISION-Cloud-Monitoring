package gr.ntua.vision.monitoring.queues;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class TopicedQueueListBean extends TopicedQueueBean {
    /***/
    private List<Map<String, Object>> value;


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
    public List<Map<String, Object>> getValue() {
        return value;
    }


    /**
     * @param value
     *            the value to set
     */
    public void setValue(final List<Map<String, Object>> value) {
        this.value = value;
    }
}
