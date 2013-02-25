package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * The storlet engine logging rule, used to aggregate logging events.
 */
public class StorletLoggingRule extends PeriodicRule {
    /**
     * 
     */
    static class StorletMessage {
        /***/
        public final String m;
        /***/
        public final long   timeStamp;


        /**
         * Constructor.
         * 
         * @param m
         * @param timeStamp
         */
        public StorletMessage(final String m, final long timeStamp) {
            this.m = m;
            this.timeStamp = timeStamp;
        }


        /**
         * @return the m
         */
        public String getM() {
            return m;
        }


        /**
         * @return the timeStamp
         */
        public long getTimeStamp() {
            return timeStamp;
        }
    }


    /**
     * 
     */
    private static class StorletLog {
        /***/
        public final String                    activationId;
        /***/
        public final ArrayList<StorletMessage> messages = new ArrayList<StorletLoggingRule.StorletMessage>();
        /***/
        public final String                    sreId;
        /***/
        public final String                    storletId;


        /**
         * Constructor.
         * 
         * @param storletId
         * @param sreId
         * @param activationId
         */
        public StorletLog(final String storletId, final String sreId, final String activationId) {
            this.storletId = storletId;
            this.sreId = sreId;
            this.activationId = activationId;
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
            final StorletLog other = (StorletLog) obj;
            if (activationId == null) {
                if (other.activationId != null)
                    return false;
            } else if (!activationId.equals(other.activationId))
                return false;
            if (sreId == null) {
                if (other.sreId != null)
                    return false;
            } else if (!sreId.equals(other.sreId))
                return false;
            if (storletId == null) {
                if (other.storletId != null)
                    return false;
            } else if (!storletId.equals(other.storletId))
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
            result = prime * result + ((activationId == null) ? 0 : activationId.hashCode());
            result = prime * result + ((sreId == null) ? 0 : sreId.hashCode());
            result = prime * result + ((storletId == null) ? 0 : storletId.hashCode());
            return result;
        }
    }

    /***/
    private static final String SRE_SERVICE = "SRE";
    /***/
    private static final String TOPIC       = "_SLLOG";


    /**
     * Constructor.
     * 
     * @param engine
     * @param period
     */
    public StorletLoggingRule(final VismoRulesEngine engine, final long period) {
        super(engine, period);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
     */
    @Override
    public void performWith(final MonitoringEvent e) {
        if (isStorletLoggingEvent(e))
            collect(e);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.PeriodicRule#aggregate(java.util.List, long, long)
     */
    @Override
    protected MonitoringEvent aggregate(final List<MonitoringEvent> eventsList, final long tStart, final long tEnd) {
        final HashMap<String, Object> dict = new HashMap<String, Object>();

        dict.put("topic", TOPIC);
        dict.put("originating-service", eventsList.get(0).originatingService());
        dict.put("tStart", tStart);
        dict.put("tEnd", tEnd);
        dict.put("groups", toJSONLike(aggregateOverMessages(eventsList)));

        return new VismoAggregationResult(dict);
    }


    /**
     * @param eventsList
     * @return a list of grouped {@link StorletLog}s.
     */
    private static ArrayList<StorletLog> aggregateOverMessages(final List<MonitoringEvent> eventsList) {
        final ArrayList<StorletLog> logs = new ArrayList<StorletLog>();

        for (final MonitoringEvent e : eventsList) {
            final StorletLog log = new StorletLog((String) e.get("storlet-id"), (String) e.get("sre-id"),
                    (String) e.get("activation-id"));
            final StorletMessage m = new StorletMessage((String) e.get("message"), e.timestamp());
            final int idx = logs.indexOf(log);

            if (idx >= 0)
                logs.get(idx).messages.add(m);
            else {
                log.messages.add(m);
                logs.add(log);
            }
        }

        return logs;
    }


    /**
     * @param e
     * @return <code>true</code> iff the events comes from the storlet engine and is a logging event.
     */
    private static boolean isStorletLoggingEvent(final MonitoringEvent e) {
        return SRE_SERVICE.equals(e.originatingService()) && e.get("activation-id") != null;
    }


    /**
     * @param logs
     * @return grouped storlet logging events.
     */
    private static ArrayList<HashMap<String, Object>> toJSONLike(final ArrayList<StorletLog> logs) {
        final ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        for (final StorletLog log : logs) {
            final HashMap<String, Object> o = new HashMap<String, Object>();

            o.put("storlet-id", log.storletId);
            o.put("activation-id", log.activationId);
            o.put("sre-id", log.sreId);
            o.put("messages", log.messages);

            list.add(o);
        }

        return list;
    }
}
