package gr.ntua.vision.monitoring.queues;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;


/**
 *
 */
public class TopicedQueueBean {

    /*
     * 
     * "objectType" : "application/cdmi-queue",
      "objectID" : "00007E7F00104BE66AB53A9572F9F51E",

    "objectName" : "MyQueue",
    "parentURI " : "/MyContainer/",
     "parentID" : "0000706D0010B84FAD185C425D8B537E",
    "domainURI" : "/cdmi_domains/MyDomain/",
    "capabilitiesURI" : "/cdmi_capabilities/queue/",
    "completionStatus" : "Complete",
    "metadata" : {
    },
    "queueValues" : ""
    */

    /***/
    private static final String              CAPABILITIES_URI     = "/cdmi_capabilities/queue/";
    /***/
    private static final String              COMPLETE_STATUS      = "Complete";
    /***/
    private static final String              DEFAULT_DOMAIN       = "/cdmi_domains/";
    /***/
    private static final Map<String, Object> DEFAULT_METADATA     = Collections.emptyMap();
    /***/
    private static final String              DEFAULT_QUEUE_VALUES = "";
    /***/
    private static final String              OBJECT_TYPE          = CDMIQueueMediaTypes.APPLICATION_CDMI_QUEUE;
    /***/
    private static final String              PARENT_ROOT          = "/";
    /***/
    private static final String              PARENT_ROOT_ID       = UUID.randomUUID().toString();

    /***/
    private String                           capabilitiesURI;
    /***/
    private String                           completionStatus;
    /***/
    private String                           domainURI;
    /***/
    private Map<String, Object>              metadata;
    /***/
    private String                           objectID;
    /***/
    private String                           objectName;
    /***/
    private String                           objectType;
    /***/
    private String                           parentID;
    /***/
    private String                           parentURI;
    /***/
    private String                           queueValues;
    /***/
    private String                           topic;


    /**
     * Constructor.
     */
    public TopicedQueueBean() {
        this.objectType = OBJECT_TYPE;
        this.objectID = UUID.randomUUID().toString();
        this.parentID = PARENT_ROOT_ID;
        this.parentURI = PARENT_ROOT;
        this.domainURI = DEFAULT_DOMAIN;
        this.capabilitiesURI = CAPABILITIES_URI;
        this.completionStatus = COMPLETE_STATUS;
        this.metadata = DEFAULT_METADATA;
        this.queueValues = DEFAULT_QUEUE_VALUES;
    }


    /**
     * @return the capabilitiesURI
     */
    public String getCapabilitiesURI() {
        return capabilitiesURI;
    }


    /**
     * @return the completionStatus
     */
    public String getCompletionStatus() {
        return completionStatus;
    }


    /**
     * @return the domainURI
     */
    public String getDomainURI() {
        return domainURI;
    }


    /**
     * @return the metadata
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }


    /**
     * @return the objectID
     */
    public String getObjectID() {
        return objectID;
    }


    /**
     * @return the objectName
     */
    public String getObjectName() {
        return objectName;
    }


    /**
     * @return the objectType
     */
    public String getObjectType() {
        return objectType;
    }


    /**
     * @return the parentID
     */
    public String getParentID() {
        return parentID;
    }


    /**
     * @return the parentURI
     */
    public String getParentURI() {
        return parentURI;
    }


    /**
     * @return the queueValues
     */
    public String getQueueValues() {
        return queueValues;
    }


    /**
     * @return the topic
     */
    public String getTopic() {
        return topic;
    }


    /**
     * @param capabilitiesURI
     *            the capabilitiesURI to set
     */
    public void setCapabilitiesURI(final String capabilitiesURI) {
        this.capabilitiesURI = capabilitiesURI;
    }


    /**
     * @param completionStatus
     *            the completionStatus to set
     */
    public void setCompletionStatus(final String completionStatus) {
        this.completionStatus = completionStatus;
    }


    /**
     * @param domainURI
     *            the domainURI to set
     */
    public void setDomainURI(final String domainURI) {
        this.domainURI = domainURI;
    }


    /**
     * @param metadata
     *            the metadata to set
     */
    public void setMetadata(final Map<String, Object> metadata) {
        this.metadata = metadata;
    }


    /**
     * @param objectID
     *            the objectID to set
     */
    public void setObjectID(final String objectID) {
        this.objectID = objectID;
    }


    /**
     * @param objectName
     *            the objectName to set
     */
    public void setObjectName(final String objectName) {
        this.objectName = objectName;
    }


    /**
     * @param objectType
     *            the objectType to set
     */
    public void setObjectType(final String objectType) {
        this.objectType = objectType;
    }


    /**
     * @param parentID
     *            the parentID to set
     */
    public void setParentID(final String parentID) {
        this.parentID = parentID;
    }


    /**
     * @param parentURI
     *            the parentURI to set
     */
    public void setParentURI(final String parentURI) {
        this.parentURI = parentURI;
    }


    /**
     * @param queueValues
     *            the queueValues to set
     */
    public void setQueueValues(final String queueValues) {
        this.queueValues = queueValues;
    }


    /**
     * @param topic
     *            the topic to set
     */
    public void setTopic(final String topic) {
        this.topic = topic;
    }
}
