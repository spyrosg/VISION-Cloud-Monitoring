package gr.ntua.vision.monitoring.queues;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * A bean which is delivered to the user by jersey when reading a CDMI queue.
 * 
 * @see CDMIQueueListBean
 */
public class CDMIQueueListBean extends CDMIQueueBean {
    /** mandatory. */
    private List<Map<String, Object>> value;


    /**
     * Constructor.
     */
    public CDMIQueueListBean() {
        super();
        this.value = Collections.emptyList();
    }


    /**
     * Constructor.
     * 
     * @param objectName
     */
    public CDMIQueueListBean(final String objectName, final String topic) {
        super(objectName, topic);
        this.value = Collections.emptyList();
    }


    /**
     * @return the value
     */
    public List<Map<String, Object>> getValue() {
        return value;
    }


    /**
     * @param list
     *            the value to set
     */
    public void setValue(final List<Map<String, Object>> list) {
        if (list.size() == 0)
            this.setQueueValues("");
        else
            this.setQueueValues("0-" + list.size());

        this.value = list;
    }
}
