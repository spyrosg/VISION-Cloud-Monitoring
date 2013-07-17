package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


/**
 * The storlet engine logging rule, used to aggregate logging events.
 */
public class StorletLoggingRule extends AggregationRule {
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
        super(engine, period, TOPIC);
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
    protected MonitoringEvent aggregate(final List<MonitoringEvent> list, final long tStart, final long tEnd) {
        final HashMap<String, Object> dict = new HashMap<String, Object>();

        dict.put("topic", TOPIC);
        dict.put("originating-service", list.get(0).originatingService());
        dict.put("tStart", tStart);
        dict.put("tEnd", tEnd);
        dict.put("groups", toMap(aggregateOverMessages(list)));

        return newAggregationEvent(dict);
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

        for (final StorletLog log : logs)
            Collections.sort(log.messages, new Comparator<StorletMessage>() {
                @Override
                public int compare(final StorletMessage o1, final StorletMessage o2) {
                    return o1.timeStamp > o2.timeStamp ? 1 : o1.timeStamp < o2.timeStamp ? -1 : 0;
                }
            });

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
    private static ArrayList<HashMap<String, Object>> toMap(final ArrayList<StorletLog> logs) {
        final ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        for (final StorletLog log : logs) {
            final HashMap<String, Object> o = new HashMap<String, Object>();

            o.put("storlet-id", log.storletId);
            o.put("activation-id", log.activationId);
            o.put("sre-id", log.sreId);

            final ArrayList<HashMap<String, Object>> messageList = new ArrayList<HashMap<String, Object>>();

            for (final StorletMessage m : log.messages) {
                final HashMap<String, Object> mm = new HashMap<String, Object>();

                mm.put("timestamp", m.timeStamp);
                mm.put("message", m.m);

                messageList.add(mm);
            }

            o.put("messages", messageList);

            list.add(o);
        }

        return list;
    }
}
