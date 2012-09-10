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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: sum of content-size and # of accesses per tenant and container
// TODO: sum and # of think-times per tenant and container
// TODO: sum and # of rethink-times per tenant and container
public class CTORule implements AggregationRule {
    public class TimestampComparator implements Comparator<Event> {
        @Override
        public int compare(final Event e1, final Event e2) {
            final long time1 = (Long) e1.get("timestamp");
            final long time2 = (Long) e2.get("timestamp");

            return time1 > time2 ? 1 : time1 < time2 ? -1 : 0;
        }
    }


    private class User {
        String            lastStatus       = "SUCCESS";
        String            name;
        ArrayList<Double> requestList      = new ArrayList();
        ArrayList<Double> responseList     = new ArrayList();
        double            reThinkTime      = 0;
        int               reThinkTimecount = 0;
        ArrayList<Double> reThinkTimeList  = new ArrayList();
        double            thinkTime        = 0;
        int               thinkTimecount   = 0;
        ArrayList<Double> thinkTimeList    = new ArrayList();


        public User(final String name, final Double responseTime, final String status, final double requestTime) {
            this.name = name;
            this.responseList.add(responseTime);
            this.lastStatus = status;
            this.requestList.add(requestTime);
        }


        public String getName() {
            return name;
        }


        public void setName(final String name) {
            this.name = name;
        }

    }
    /***/
    private static final String AGGREGATION_FIELD = "content-size";
    /***/
    private static final String DICT              = "!dict";
    /***/
    private static final Logger log               = LoggerFactory.getLogger(CTORule.class);
    /***/
    private static final String SPECIAL_FIELD     = "transaction-duration";

    private static final String TOPIC             = "CTO";

    /***/
    private final String        operation;


    /**
     * @param aggregationField
     * @param resultField
     */
    public CTORule(final String operation) {
        this.operation = operation;
    }


    @SuppressWarnings("unchecked")
    @Override
    public AggregationResultEvent aggregate(final long aggregationStartTime, final List< ? extends Event> eventList) {
        @SuppressWarnings("rawtypes")
        final Map dict = getFinalObject(eventList, aggregationStartTime);
        aggregateThinkTime(eventList);

        return new VismoAggregationResultEvent(dict);
    }


    @Override
    public boolean matches(final Event e) {
        final String op = (String) e.get("operation");

        // FIXME: add a field for events coming from vismo_dispatch
        return e.get(SPECIAL_FIELD) != null && op.equals(operation);
    }


    @Override
    public String toString() {
        return "#<" + this.getClass().getSimpleName() + "[" + operation + "]>";
    }


    private void aggregateThinkTime(final List< ? extends Event> eventList) {

        // sort list
        Collections.sort(eventList, new TimestampComparator());

        // create user list
        final List<User> userList = new ArrayList<User>();

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

        printList(userList);
        // final ArrayList<Map<String, Object>>
        // final HashMap<String, Object> u = new HashMap<String, Object>();

    }


    /* SPYROS */

    private HashSet<Container> getContentSizePerContainer(final List< ? extends Event> eventList) {
        final HashSet<Container> containers = new HashSet<Container>();

        for (final Event e : eventList) {
            final String tenant = (String) e.get("tenant");
            final String containerName = (String) e.get("container");
            final long size = getFieldValueAsLong(e, "content-size");
            final Container c = new Container(tenant, containerName, size);

            if (containers.contains(c))
                for (final Container cc : containers) {
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


    private HashMap<String, Object> getFinalObject(final List< ? extends Event> eventList, final long aggregationStartTime) {
        final HashSet<Container> containers = getContentSizePerContainer(eventList);
        final HashMap<String, HashMap<String, Object>> tenants = new HashMap<String, HashMap<String, Object>>();

        for (final Container c : containers) {
            final HashMap<String, Object> tenant = tenants.get(c.tenant);

            if (tenant == null) {
                final HashMap<String, Object> newTenant = new HashMap<String, Object>();
                final ArrayList<Map<String, Object>> containerList = new ArrayList<Map<String, Object>>();
                final HashMap<String, Object> container = new HashMap<String, Object>();

                container.put("name", c.name);
                container.put("size-sum", c.getSize());
                container.put("size-count", c.getAccessess());

                containerList.add(container);

                newTenant.put("name", c.tenant);
                newTenant.put("containers", containerList);

                tenants.put(c.tenant, newTenant);
            } else {
                @SuppressWarnings("unchecked")
                final ArrayList<Map<String, Object>> containerList = (ArrayList<Map<String, Object>>) tenant.get("containers");

                final HashMap<String, Object> container = new HashMap<String, Object>();

                container.put("name", c.name);
                container.put("size-sum", c.getSize());
                container.put("size-count", c.getAccessess());

                containerList.add(container);
            }
        }

        System.err.println("containers: " + containers);

        final HashMap<String, Object> dict = new HashMap<String, Object>();

        dict.put("tenants", tenants);
        dict.put("topic", TOPIC);
        dict.put("tStart", aggregationStartTime);
        dict.put("tEnd", System.currentTimeMillis());

        return dict;
    }


    private void printList(final List<User> userList) {

        final Iterator<User> userIterator = userList.iterator();
        while (userIterator.hasNext()) {
            final User tmp = userIterator.next();
            System.out.println("Name= " + tmp.name + " Think Count= " + tmp.thinkTimecount + " Thinktime= " + tmp.thinkTime
                    + " ReThink Time Count " + tmp.reThinkTimecount + " ReThinkTime= " + tmp.reThinkTime);
        }

    }


    private static Long getFieldValueAsLong(final Event e, final String field) {
        final Object val = e.get(field);

        if (val == null) {
            log.trace("event missing required field '{}'; skipping", AGGREGATION_FIELD);
            log.trace("event: {}", e);

            return null;
        }

        if (val instanceof String)
            return Long.valueOf((String) val);

        try {
            return (Long) val;
        } catch (final ClassCastException x) {
            log.trace("expecting field '{}' of type {} ...", field, Long.class);
            log.trace("but got value {} of type {}", val, val.getClass());
            log.trace("", x);

            return null;
        }
    }
}
