package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO: sum of content-size and # of accesses per tenant and container TODO: sum and # of think-times per tenant and container
 * TODO: sum and # of rethink-times per tenant and container
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


    /**
     * 
     */
    private class User {
        String                  lastStatus       = "SUCCESS";
        String                  name;
        final ArrayList<Double> requestList      = new ArrayList<Double>();
        final ArrayList<Double> responseList     = new ArrayList<Double>();
        double                  reThinkTime      = 0;
        int                     reThinkTimecount = 0;
        final ArrayList<Double> reThinkTimeList  = new ArrayList<Double>();
        double                  thinkTime        = 0;
        int                     thinkTimecount   = 0;
        final ArrayList<Double> thinkTimeList    = new ArrayList<Double>();


        /**
         * Constructor.
         * 
         * @param name
         * @param responseTime
         * @param status
         * @param requestTime
         */
        public User(final String name, final Double responseTime, final String status, final double requestTime) {
            this.name = name;
            this.responseList.add(responseTime);
            this.lastStatus = status;
            this.requestList.add(requestTime);
        }


        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return toJSONString();
        }


        /**
         * @return
         */
        @SuppressWarnings("unchecked")
        public String toJSONString() {
            final JSONObject o = new JSONObject();

            o.put("name", name);
            o.put("sum-think-time", this.thinkTime);
            o.put("count-think-time", this.thinkTimecount);
            o.put("sum-rethink-time", this.reThinkTime);
            o.put("count-rethink-time", this.reThinkTimecount);

            return o.toString();
        }
    }

    /***/
    private static final String AGGREGATION_FIELD = "content-size";
    /***/
    private static final Logger log               = LoggerFactory.getLogger(CTORule.class);
    /***/
    private static final String SPECIAL_FIELD     = "transaction-duration";
    /***/
    private static final String TOPIC             = "CTO";
    /***/
    private final String        operation;


    /**
     * Constructor.
     * 
     * @param operation
     */
    public CTORule(final String operation) {
        this.operation = operation;
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationRule#aggregate(long, java.util.List)
     */
    @SuppressWarnings("unchecked")
    @Override
    public AggregationResultEvent aggregate(final long aggregationStartTime, final List< ? extends Event> eventList) {
        @SuppressWarnings("rawtypes")
        final Map dict = getCTOEvent(eventList, aggregationStartTime);

        return new VismoAggregationResultEvent(dict);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationRule#matches(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public boolean matches(final Event e) {
        final String op = (String) e.get("operation");

        // FIXME: add a field for events coming from vismo_dispatch
        return e.get(SPECIAL_FIELD) != null && op.equals(operation);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<" + this.getClass().getSimpleName() + "[" + operation + "]>";
    }


    /**
     * @param eventList
     * @return
     */
    private ArrayList<User> aggregateThinkTime(final List< ? extends Event> eventList) {
        // sort list
        Collections.sort(eventList, new TimestampComparator());

        // create user list
        final ArrayList<User> userList = new ArrayList<User>();

        for (final Event e : eventList)
            try {
                // final String tenant = (String) e.get("tenant");
                // final String name = (String) e.get("container");
                // final Double size = getDoubleValue(e, aggregationField);
                final String tenant = (String) e.get("tenant");
                final String user = (String) e.get("user");
                // final String userTenant = userName+"@"+tenant;
                final String userName = user + "@" + tenant;
                final double transactionTime = ((Double) e.get("transaction-duration")) / 1000.0; // transactionTime turned to
                                                                                                  // millis
                // final Double timestamp = ((Double) e.get("timestamp"))-transactionTime;
                final double responseTime = (Long) e.get("timestamp");
                final Double requestTime = ((Long) e.get("timestamp")) - transactionTime;
                boolean foundUser = false;
                final String status = (String) e.get("status");

                // SUCCESS
                // FAIL
                // THROTTLING

                /*
                 * if (size == null) { log.trace("event with no appropriate field '{}'; skipping", aggregationField); continue; }
                 */

                if (userList.isEmpty())
                    userList.add(new User(userName, responseTime, status, requestTime));
                else {

                    final Iterator<User> iterator = userList.iterator();

                    while (iterator.hasNext()) {
                        final User tmp = iterator.next();
                        if (tmp.name.equals(userName)) {// found user

                            if ((tmp.lastStatus.equalsIgnoreCase("SUCCESS")) || (tmp.lastStatus.equalsIgnoreCase("FAIL"))) { // last
                                                                                                                             // was
                                                                                                                             // a
                                                                                                                             // success
                                                                                                                             // or
                                                                                                                             // a
                                                                                                                             // failure
                                                                                                                             // therefore
                                                                                                                             // we
                                                                                                                             // calculate
                                                                                                                             // thinkTime

                                if (!tmp.responseList.isEmpty()) {
                                    tmp.thinkTimeList.add(requestTime - tmp.responseList.get(tmp.responseList.size() - 1));
                                    tmp.responseList.add(responseTime);
                                    tmp.requestList.add(requestTime);
                                } else {
                                    tmp.responseList.add(responseTime);
                                    tmp.requestList.add(requestTime);
                                }
                                // tmp.thinkTimeList.add(responseTime);
                                // foundUser=true;
                                // break;
                                tmp.lastStatus = new String(status);
                            }

                            else if (tmp.lastStatus.equalsIgnoreCase("THROTTLING")) {
                                if (!tmp.responseList.isEmpty()) {
                                    tmp.reThinkTimeList.add(requestTime - tmp.responseList.get(tmp.responseList.size() - 1));
                                    tmp.responseList.add(responseTime);
                                    tmp.requestList.add(requestTime);
                                } else {
                                    tmp.responseList.add(responseTime);
                                    tmp.requestList.add(requestTime);
                                }
                                // tmp.thinkTimeList.add(responseTime);
                                // foundUser=true;
                                tmp.lastStatus = new String(status);

                            }

                            foundUser = true;
                            break;
                        }

                    }

                    if (!foundUser)
                        userList.add(new User(userName, responseTime, status, requestTime));

                }

            } catch (final Throwable x) {
                x.printStackTrace();
                log.error("continuing aggregation (think time exploded", x);
            }

        final Iterator<User> userIterator = userList.iterator();

        // do the sums now
        while (userIterator.hasNext()) {
            final User tmp = userIterator.next();
            final Iterator<Double> thinkTimeIterator = tmp.thinkTimeList.iterator();
            final Iterator<Double> reThinkTimeIterator = tmp.reThinkTimeList.iterator();
            while (thinkTimeIterator.hasNext()) {
                tmp.thinkTime = tmp.thinkTime + thinkTimeIterator.next();
                tmp.thinkTimecount++;
            }
            while (reThinkTimeIterator.hasNext()) {
                tmp.reThinkTime = tmp.reThinkTime + reThinkTimeIterator.next();
                tmp.reThinkTimecount++;
            }

        }

        return userList;
    }


    /**
     * @param eventList
     * @return
     */
    private static HashSet<CTOContainerObject> getContentSizePerContainer(final List< ? extends Event> eventList) {
        final HashSet<CTOContainerObject> containers = new HashSet<CTOContainerObject>();

        for (final Event e : eventList) {
            final String tenant = (String) e.get("tenant");
            final String containerName = (String) e.get("container");
            final long size = getFieldValueAsLong(e, "content-size");
            final CTOContainerObject c = new CTOContainerObject(tenant, containerName, size);

            if (containers.contains(c))
                for (final CTOContainerObject cc : containers) {
                    if (cc.name.equals(containerName) && cc.tenant.equals(tenant)) {
                        cc.addObjectSize(size);
                        cc.incAccesses();
                    }
                }
            else
                containers.add(c);
        }

        return containers;
    }


    /**
     * @param e
     * @param field
     * @return
     */
    private static Long getFieldValueAsLong(final Event e, final String field) {
        final Object val = e.get(field);

        if (val == null) {
            log.warn("missing required field '{}' or is null; returning 0", AGGREGATION_FIELD);

            return 0l;
        }

        if (val instanceof String) {
            log.warn("required field '{}' should be {}; try to parse it", AGGREGATION_FIELD, Long.class);

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
     * @param aggregationStartTime
     * @return
     */
    private HashMap<String, Object> getCTOEvent(final List< ? extends Event> eventList, final long aggregationStartTime) {
        final HashSet<CTOContainerObject> containers = getContentSizePerContainer(eventList);
        final ArrayList<CTOTenantObject> tenantsList = new ArrayList<CTOTenantObject>();

        for (final String tenant : getTenantList(eventList)) {
            final CTOTenantObject ctot = new CTOTenantObject(tenant);

            for (final CTOContainerObject cont : containers) {
                if (cont.tenant.equals(tenant))
                    ctot.addContainer(cont);
            }

            tenantsList.add(ctot);
        }

        final ArrayList<User> userList = aggregateThinkTime(eventList);

        final HashMap<String, Object> dict = new HashMap<String, Object>();

        dict.put("tenants", tenantsList);
        dict.put("users", userList);
        dict.put("topic", TOPIC);
        dict.put("tStart", aggregationStartTime);
        dict.put("tEnd", System.currentTimeMillis());

        return dict;
    }


    /**
     * @param eventList
     * @return
     */
    private static ArrayList<String> getTenantList(final List< ? extends Event> eventList) {
        final HashSet<String> set = new HashSet<String>();

        for (final Event e : eventList) {
            final String tenant = (String) e.get("tenant");

            set.add(tenant);
        }

        return new ArrayList<String>(set);
    }
}
