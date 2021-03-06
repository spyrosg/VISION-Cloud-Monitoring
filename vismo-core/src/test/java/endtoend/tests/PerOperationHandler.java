package endtoend.tests;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 * This is used to verify that the expected number of events for given operation were received. We only care about operations on
 * objects, not containers.
 */
class PerOperationHandler extends NoEventsCheckingHandler {
    /***/
    private static final String ORIGINATING_SERVICE = "object_service";
    /***/
    private final String        operation;


    /**
     * Constructor.
     * 
     * @param operation
     */
    public PerOperationHandler(final String operation) {
        this.operation = operation;
    }


    /**
     * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.MonitoringEvent)
     */
    @Override
    public void handle(final MonitoringEvent e) {
        if (!isObsEvent(e))
            return;
        if (isContainerOperationEvent(e))
            return;
        if (e.topic() != null)
            return;

        if (operation.equals(e.get("operation")))
            receivedEvent(e);
    }


    /**
     * @param e
     * @return <code>true</code> iff the event represents an operation on a container, <code>false</code> otherwise.
     */
    private static boolean isContainerOperationEvent(final MonitoringEvent e) {
        final String objectName = (String) e.get("object");

        return objectName == null || objectName.isEmpty();
    }


    /**
     * @param e
     * @return <code>true</code> iff this is an event that comes from the obs, <code>false</code> otherwise.
     */
    private static boolean isObsEvent(final MonitoringEvent e) {
        return ORIGINATING_SERVICE.equals(e.originatingService());
    }
}
