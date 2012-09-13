package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to implement the CTO rule. It aggregates over containers and objects. More specifically it provides the following
 * metrics:
 * <ol>
 * <li>the sum content-size (in bytes) for each container and tenant (field: sum-size)</li>
 * <li>the # of accesses for each container and tenant (field: count-size)</li>
 * <li>the sum of think-times (in seconds) per user (field: sum-think-time)</li>
 * <li>the # of in between requests on any object, per tenant and container (field: count-think-time)</li>
 * <li>the sum of re-think-time (in seconds) per user (field: sum-rethink-time)</li>
 * <li>the # of in between <strong>DIFFERENT</strong> responses per user (field: count-rethink-time)</li>
 * </ol>
 */
public class CTORule implements AggregationRule {
    /**
     * 
     */
    public class TimestampComparator implements Comparator<Event> {
        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(final Event e1, final Event e2) {
            final long time1 = (Long) e1.get("timestamp");
            final long time2 = (Long) e2.get("timestamp");

            return time1 > time2 ? 1 : time1 < time2 ? -1 : 0;
        }
    }

    /***/
    private static final String CONTENT_SIZE_FIELD         = "content-size";
    /***/
    private static final String GET_OPERATION              = "GET";
    /***/
    private static final Logger log                        = LoggerFactory.getLogger(CTORule.class);
    /***/
    private static final String OPERATION_FIELD            = "operation";
    /***/
    private static final String PUT_OPERATION              = "PUT";
    /***/
    private static final String SEP                        = "/#@$!/";
    /***/
    private static final String SPECIAL_FIELD              = "transaction-duration";
    /***/
    private static final String THROTTLING                 = "THROTTLING";
    /***/
    private static final String TRANSACTION_DURATION_FIELD = "transaction-duration";
    /***/
    private final long          period;
    /***/
    private final String        topic;


    /**
     * Constructor.
     * 
     * @param topic
     * @param period
     */
    public CTORule(final String topic, final long period) {
        this.topic = topic;
        this.period = period;
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationRule#aggregate(long, java.util.List)
     */
    @SuppressWarnings("unchecked")
    @Override
    public AggregationResultEvent aggregate(final long aggregationStartTime, final List< ? extends Event> eventList) {
        @SuppressWarnings("rawtypes")
        final Map dict = getCTOEvent(eventList, topic, aggregationStartTime);

        return new VismoAggregationResultEvent(dict);
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
        final CTORule other = (CTORule) obj;
        if (period != other.period)
            return false;
        if (topic == null) {
            if (other.topic != null)
                return false;
        } else if (!topic.equals(other.topic))
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
        result = prime * result + (int) (period ^ (period >>> 32));
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        return result;
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationRule#matches(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public boolean matches(final Event e) {
        // FIXME: add a field for events coming from vismo_dispatch
        return e.get(SPECIAL_FIELD) != null;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<" + this.getClass().getSimpleName() + ", topic: " + topic + ", running every " + (period / 1000)
                + " second(s)>";
    }


    /**
     * @param eventList
     * @return
     */
    private static ArrayList<HashMap<String, Object>> aggregate(final List< ? extends Event> eventList) {
        final HashMap<ContainerRequest, RequestCTOStats> requestsByUser = aggregateOverUsers(eventList);
        final ArrayList<HashMap<String, Object>> containerList = aggregateOverContainers(requestsByUser);

        return aggregateOverTenants(containerList);
    }


    /**
     * @param requestsByUser
     * @return
     */
    @SuppressWarnings("unchecked")
    private static ArrayList<HashMap<String, Object>> aggregateOverContainers(
            final HashMap<ContainerRequest, RequestCTOStats> requestsByUser) {
        final ArrayList<HashMap<String, Object>> containers = new ArrayList<HashMap<String, Object>>();

        // first pass, put in there all different containers
        for (final ContainerRequest req : requestsByUser.keySet())
            containers.add(getContainerObject(req.tenant + SEP + req.container));

        // second pass, append users by tenant/container
        for (final ContainerRequest req : requestsByUser.keySet())
            for (final HashMap<String, Object> c : containers) {
                final String containerName = (String) c.get("name");

                if (containerName.equals(req.tenant + SEP + req.container)) {
                    final ArrayList<HashMap<String, Object>> userList = (ArrayList<HashMap<String, Object>>) c.get("users");

                    userList.add(getUserObject(req.user, requestsByUser.get(req)));
                }
            }

        return containers;
    }


    /**
     * @param containerList
     * @return
     */
    private static ArrayList<HashMap<String, Object>> aggregateOverTenants(final ArrayList<HashMap<String, Object>> containerList) {
        final ArrayList<HashMap<String, Object>> tenantList = new ArrayList<HashMap<String, Object>>();
        final HashSet<String> uniqueTenantNames = new HashSet<String>();

        // first pass, put in there all different tenants
        for (final HashMap<String, Object> container : containerList) {
            final String tenantContainerName = (String) container.get("name");
            final int index = tenantContainerName.indexOf(SEP);
            final String tenantName = tenantContainerName.substring(0, index);

            if (!uniqueTenantNames.contains(tenantName)) {
                uniqueTenantNames.add(tenantName);

                final HashMap<String, Object> tenant = new HashMap<String, Object>();

                tenant.put("name", tenantName);
                tenant.put("containers", new ArrayList<HashMap<String, Object>>());

                tenantList.add(tenant);
            }
        }

        // second pass, assign containers to tenants.
        for (final HashMap<String, Object> container : containerList) {
            final String tenantContainerName = (String) container.get("name");

            for (final HashMap<String, Object> tenant : tenantList) {
                final String tenantName = (String) tenant.get("name");

                if (tenantContainerName.startsWith(tenantName)) {
                    @SuppressWarnings("unchecked")
                    final ArrayList<HashMap<String, Object>> containerList1 = (ArrayList<HashMap<String, Object>>) tenant
                            .get("containers");

                    final int index = tenantContainerName.indexOf(SEP);
                    final String containerName = tenantContainerName.substring(index + SEP.length());

                    container.put("name", containerName);
                    containerList1.add(container);
                }
            }
        }

        return tenantList;
    }


    /**
     * @param eventList
     * @return
     */
    private static HashMap<ContainerRequest, RequestCTOStats> aggregateOverUsers(final List< ? extends Event> eventList) {
        final HashMap<ContainerRequest, RequestCTOStats> requests = new HashMap<ContainerRequest, RequestCTOStats>();
        Event prev = null;

        for (final Event e : eventList) {
            final ContainerRequest cu = new ContainerRequest((String) e.get("tenant"), (String) e.get("container"),
                    (String) e.get("user"));
            final Long contentSize = getFieldValueAsLong(e, CONTENT_SIZE_FIELD);
            final Double transactionDuration = getFieldValueAsDouble(e, TRANSACTION_DURATION_FIELD);

            if (requests.containsKey(cu)) {
                final RequestCTOStats rs = requests.get(cu);

                rs.sumContentSize(contentSize);
                rs.incAccesses();
                calculateUserProcessingTime(prev, e, rs);
                rs.addTransactionTime(transactionDuration);
            } else {
                final RequestCTOStats rs = new RequestCTOStats(contentSize);

                calculateUserProcessingTime(prev, e, rs);
                rs.addTransactionTime(transactionDuration);
                requests.put(cu, rs);
            }

            prev = e;
        }

        return requests;
    }


    /**
     * This method is used to calculate either the user's think-time or re-think-time, the number of milliseconds it took the user
     * to make a new request, since he last got a response. If the last user request was throttled, the timediff is appended to
     * the re-think-time list, else it is appended to the think-time one.
     * 
     * @param prev
     * @param curr
     * @param rs
     */
    private static void calculateUserProcessingTime(final Event prev, final Event curr, final RequestCTOStats rs) {
        if (prev == null) // nothing to do on the first element
            return;

        // NOTE: transaction duration turned to millis
        final double currentRequestTime = curr.timestamp() - (((Double) curr.get("transaction-duration")) / 1000.0);
        final double prevResponseTime = prev.timestamp();
        final String status = (String) prev.get("status");

        if (status.equalsIgnoreCase(THROTTLING))
            rs.addReThinkTime(currentRequestTime - prevResponseTime);
        else
            rs.addThinkTime(currentRequestTime - prevResponseTime);
    }


    /**
     * @param containerName
     * @return
     */
    private static HashMap<String, Object> getContainerObject(final String containerName) {
        final HashMap<String, Object> container = new HashMap<String, Object>();

        container.put("name", containerName);
        container.put("users", new ArrayList<HashMap<String, Object>>());

        return container;
    }


    /**
     * @param eventList
     * @param topic
     * @param aggregationStartTime
     * @return
     */
    private static HashMap<String, Object> getCTOEvent(final List< ? extends Event> eventList, final String topic,
            final long aggregationStartTime) {
        final List<Event> readEventList = getReadEventsList(eventList);
        final List<Event> writeEventList = getWriteEventsList(eventList);
        final HashMap<String, Object> reads = new HashMap<String, Object>();
        final HashMap<String, Object> writes = new HashMap<String, Object>();
        final HashMap<String, Object> dict = new HashMap<String, Object>();

        reads.put("tenants", aggregate(readEventList));
        writes.put("tenants", aggregate(writeEventList));

        dict.put("reads", reads);
        dict.put("writes", writes);

        dict.put("topic", topic);
        dict.put("tStart", aggregationStartTime);
        dict.put("tEnd", System.currentTimeMillis());

        return dict;
    }


    /**
     * @param e
     * @param field
     * @return
     */
    private static Double getFieldValueAsDouble(final Event e, final String field) {
        final Object val = e.get(field);

        if (val == null) {
            log.warn("missing required field '{}' or is null; returning 0", field);

            return 0d;
        }

        if (val instanceof String) {
            log.warn("required field '{}' should be {}; try to parse it", field, Long.class);

            return Double.valueOf((String) val);
        }

        try {
            return (Double) val;
        } catch (final ClassCastException x) {
            log.trace("expecting field '{}' of type {} ...", field, Long.class);
            log.trace("but got value {} of type {}", val, val.getClass());
            log.trace("exception: ", x);

            return null;
        }
    }


    /**
     * @param e
     * @param field
     * @return
     */
    private static Long getFieldValueAsLong(final Event e, final String field) {
        final Object val = e.get(field);

        if (val == null) {
            log.warn("missing required field '{}' or is null; returning 0", field);

            return 0l;
        }

        if (val instanceof String) {
            log.warn("required field '{}' should be {}; try to parse it", field, Long.class);

            return Long.valueOf((String) val);
        }

        try {
            return (Long) val;
        } catch (final ClassCastException x) {
            log.trace("expecting field '{}' of type {} ...", field, Long.class);
            log.trace("but got value {} of type {}", val, val.getClass());
            log.trace("exception: ", x);

            return null;
        }
    }


    /**
     * @param eventList
     * @return
     */
    private static ArrayList<Event> getReadEventsList(final List< ? extends Event> eventList) {
        final ArrayList<Event> newList = new ArrayList<Event>();

        for (final Event e : eventList)
            if (((String) e.get(OPERATION_FIELD)).equalsIgnoreCase(GET_OPERATION))
                newList.add(e);

        return newList;
    }


    /**
     * @param userName
     * @param stats
     * @return
     */
    private static HashMap<String, Object> getUserObject(final String userName, final RequestCTOStats stats) {
        final HashMap<String, Object> user = new HashMap<String, Object>();

        user.put("name", userName);

        user.put("sum-size", stats.getContentSizeSum());
        user.put("count-size", stats.getNoOfContainerAccesses());

        user.put("sum-think-time", stats.sumThinkTimes() / 1000.0);
        user.put("count-think-time", stats.getThinkTimesCount());

        user.put("sum-rethink-time", stats.sumReThinkTimes() / 1000.0);
        user.put("count-rethink-time", stats.getReThinkTimesCount());

        user.put("sum-transaction-time", stats.sumTransactionTimes() / 1000.0);

        return user;
    }


    /**
     * @param eventList
     * @return
     */
    private static ArrayList<Event> getWriteEventsList(final List< ? extends Event> eventList) {
        final ArrayList<Event> newList = new ArrayList<Event>();

        for (final Event e : eventList)
            if (((String) e.get(OPERATION_FIELD)).equalsIgnoreCase(PUT_OPERATION))
                newList.add(e);

        return newList;
    }
}
