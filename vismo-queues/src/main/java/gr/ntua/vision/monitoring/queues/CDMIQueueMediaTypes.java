package gr.ntua.vision.monitoring.queues;

import javax.ws.rs.core.MediaType;


/**
 * CDMI Queue media types.
 */
public class CDMIQueueMediaTypes extends MediaType {
    /***/
    public static final String    APPLICATION_CDMI_QUEUE      = "application/cdmi-queue";
    /***/
    public static final MediaType APPLICATION_CDMI_QUEUE_TYPE = new MediaType("application", "cdmi-queue");
    /***/
    public static final String    X_CDMI                      = "X-CDMI-Specification-Version";
    /***/
    public static final String    X_CDMI_VERSION              = "1.0.2";
}
