package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 
 */
public class SLARule extends AbstractAggregationRule {
    /**
     * 
     */
    private static class ContainerTenant {
        /** the name of the container. */
        public final String name;
        /***/
        public final String tenant;


        /**
         * Constructor.
         * 
         * @param tenant
         * @param name
         */
        public ContainerTenant(final String tenant, final String name) {
            this.tenant = tenant;
            this.name = name;
        }


        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final ContainerTenant other = (ContainerTenant) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (tenant == null) {
                if (other.tenant != null)
                    return false;
            } else if (!tenant.equals(other.tenant))
                return false;
            return true;
        }


        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((tenant == null) ? 0 : tenant.hashCode());
            return result;
        }
    }


    /**
     * 
     */
    private static class RequestSLAStats {
        /***/
        private final long noObjectsCreated      = 0;
        /***/
        private final long noRequests            = 0;
        /***/
        private final long noSuccessfulResponses = 0;
        /***/
        private final long sumThroughput         = 0;
    }

    /***/
    private static final String STORLET_FIELD = "storlet";
    /***/
    private static final String TOPIC         = "sla";


    /**
     * Constructor.
     * 
     * @param period
     */
    public SLARule(final long period) {
        super(TOPIC, period);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationRule#aggregate(java.util.List)
     */
    @Override
    public AggregationResult aggregate(final List< ? extends Event> eventList) {
        final HashMap<String, Object> dict = getSLAEventObject(eventList);

        return new VismoAggregationResult(dict);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationRule#matches(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public boolean matches(final Event e) {
        // FIXME: add a field to events coming from vismo_dispatch
        return isCompleteObsEvent(e);
    }


    /**
     * @param eventList
     * @return
     */
    private HashMap<String, Object> getSLAEventObject(final List< ? extends Event> eventList) {
        final HashMap<String, Object> dict = new HashMap<String, Object>();

        dict.put("throughput", null);
        dict.put("no-objects-created", null);
        dict.put("no-requests", null);
        dict.put("no-responses", null);
        dict.put("no-storlets", null);
        dict.put("storlet-execution-time", null);
        dict.put(topic, TOPIC);

        return dict;
    }


    /**
     * @param eventList
     * @return
     */
    private static HashMap<ContainerTenant, RequestSLAStats> getPerRequest(final List< ? extends Event> eventList) {
        final HashMap<ContainerTenant, RequestSLAStats> requests = new HashMap<ContainerTenant, RequestSLAStats>();

        return requests;
    }


    /**
     * @param eventList
     * @return
     */
    private static ArrayList<Event> selectObsEvents(final List< ? extends Event> eventList) {
        return selectEventsByField(eventList, OBS_FIELD);
    }


    /**
     * @param eventList
     * @return
     */
    private static ArrayList<Event> selectStorletEvents(final List< ? extends Event> eventList) {
        return selectEventsByField(eventList, STORLET_FIELD);
    }
}
