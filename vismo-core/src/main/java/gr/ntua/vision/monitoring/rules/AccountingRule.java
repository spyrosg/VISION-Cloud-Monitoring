package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 
 */
public class AccountingRule extends VismoAggregationRule {
    /**
     *
     */
    private static class Storlet {
        /***/
        long         noTriggers       = 0;
        /***/
        final String storletCodeType;
        /***/
        final String storletType;
        /***/
        long         sumExecutionTime = 0;


        /**
         * Constructor.
         * 
         * @param storletCodeType
         * @param storletType
         */
        public Storlet(final String storletCodeType, final String storletType) {
            this.storletCodeType = storletCodeType;
            this.storletType = storletType;
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
            final Storlet other = (Storlet) obj;
            if (storletCodeType == null) {
                if (other.storletCodeType != null)
                    return false;
            } else if (!storletCodeType.equals(other.storletCodeType))
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
            result = prime * result + ((storletCodeType == null) ? 0 : storletCodeType.hashCode());
            return result;
        }
    }


    /**
     * 
     */
    private static class TenantStorlets {
        /***/
        final ArrayList<Storlet> storlets = new ArrayList<AccountingRule.Storlet>();
        /***/
        private final String     tenant;


        /**
         * Constructor.
         * 
         * @param tenant
         */
        public TenantStorlets(final String tenant) {
            this.tenant = tenant;
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
            final TenantStorlets other = (TenantStorlets) obj;
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
            result = prime * result + ((tenant == null) ? 0 : tenant.hashCode());
            return result;
        }
    }

    /** one second in milliseconds */
    private static long         MILLIS = 1000;
    /***/
    private static final String TOPIC  = "Accounting";


    /**
     * Constructor.
     * 
     * @param period
     */
    public AccountingRule(final long period) {
        super(TOPIC, period);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationRule#aggregate(java.util.List)
     */
    @SuppressWarnings("unchecked")
    @Override
    public AggregationResult aggregate(final List< ? extends Event> eventList) {
        @SuppressWarnings("rawtypes")
        final HashMap dict = getAccountingEventObject(eventList);

        return new VismoAggregationResult(dict);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.AggregationRule#matches(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public boolean matches(final Event e) {
        // FIXME: add a field to events coming from vismo_dispatch
        return isCompleteObsEvent(e) || isStorletEngineEvent(e);
    }


    /**
     * Assemble the accounting stats object.
     * 
     * @param eventList
     * @return the accounting stats object.
     */
    private static HashMap<String, Object> getAccountingEventObject(final List< ? extends Event> eventList) {
        final HashMap<String, Object> dict = new HashMap<String, Object>();

        dict.put("reads", transformReadList(selectReadEvents(eventList)));
        dict.put("writes", transformWriteList(selectWriteEvents(eventList)));
        dict.put("deletes", transformDeleteList(selectDeleteEvents(eventList)));
        dict.put("storlet", transformStorletEvents(selectStorletEngineEvents(eventList)));
        dict.put("topic", TOPIC);

        return dict;
    }


    /**
     * Transform each event in the list according to the operation.
     * 
     * @param list
     * @param operation
     * @return the transformed list.
     */
    private static ArrayList<HashMap<String, Object>> transformByOperation(final ArrayList<Event> list, final String operation) {
        final ArrayList<HashMap<String, Object>> newList = new ArrayList<HashMap<String, Object>>(list.size());

        for (final Event e : list) {
            final HashMap<String, Object> o = transformEvent(e);

            o.put("eventType", operation);
            newList.add(o);
        }

        return newList;
    }


    /**
     * @param eventList
     * @return the list of delete events as prescribed by accounting.
     */
    private static ArrayList<HashMap<String, Object>> transformDeleteList(final ArrayList<Event> eventList) {
        return transformByOperation(eventList, "delete");
    }


    /**
     * @param e
     * @return the event object as prescribed by accounting.
     */
    private static HashMap<String, Object> transformEvent(final Event e) {
        final HashMap<String, Object> o = new HashMap<String, Object>();
        final long ts = e.timestamp();
        final double duration = MILLIS * (Double) e.get("transaction-duration");

        o.put("tStart", (long) (ts - duration));
        o.put("tEnd", ts);

        o.put("tenantID", e.get("tenant"));
        o.put("userID", e.get("user"));
        o.put("containerID", e.get("container"));
        o.put("objectID", e.get("object"));
        o.put("service", e.originatingService());

        o.put("count", 1);
        // NOTE: N/A o.put("value",);
        o.put("bandwidth", e.get("transaction-throughput"));
        o.put("replicas", 1);

        o.put("size", e.get("content-size"));

        return o;
    }


    /**
     * @param eventList
     * @return the list of read events as prescribed by accounting.
     */
    private static ArrayList<HashMap<String, Object>> transformReadList(final ArrayList<Event> eventList) {
        return transformByOperation(eventList, "read");
    }


    /**
     * Assemble the storlet aggregated events.
     * 
     * @param list
     *            the event list.
     * @return the storlet aggregated events.
     */
    private static ArrayList<HashMap<String, Object>> transformStorletEvents(final ArrayList<Event> list) {
        final HashMap<String, TenantStorlets> tenants = new HashMap<String, AccountingRule.TenantStorlets>();

        for (final Event e : list) {
            final String tenantName = (String) e.get("tenantID");
            final TenantStorlets entry = tenants.get(tenantName);
            final Storlet s = new Storlet((String) e.get("storletCodeType"), (String) e.get("storletType"));

            if (entry == null) {
                final TenantStorlets tenant = new TenantStorlets(tenantName);

                s.noTriggers = 1;
                s.sumExecutionTime = (Long) e.get("end_time") - (Long) e.get("start_time");
                tenant.storlets.add(s);
                tenants.put(tenantName, tenant);
            } else {
                final int ind = entry.storlets.indexOf(s);

                if (ind < 0) {
                    s.noTriggers = 1;
                    s.sumExecutionTime = (Long) e.get("end_time") - (Long) e.get("start_time");
                    entry.storlets.add(s);
                } else {
                    ++s.noTriggers;
                    s.sumExecutionTime += (Long) e.get("end_time") - (Long) e.get("start_time");
                }
            }
        }

        final ArrayList<HashMap<String, Object>> storletList = new ArrayList<HashMap<String, Object>>();

        for (final String tenantName : tenants.keySet())
            for (final Storlet s : tenants.get(tenantName).storlets) {
                final HashMap<String, Object> ss = new HashMap<String, Object>();

                ss.put("storletCodeType", s.storletCodeType);
                ss.put("storletType", s.storletType);
                ss.put("executionTime", s.sumExecutionTime);
                ss.put("count", s.noTriggers);
                ss.put("tenantID", tenantName);

                storletList.add(ss);
            }

        return storletList;
    }


    /**
     * @param eventList
     * @return the list of write events as prescribed by accounting.
     */
    private static ArrayList<HashMap<String, Object>> transformWriteList(final ArrayList<Event> eventList) {
        return transformByOperation(eventList, "write");
    }
}
