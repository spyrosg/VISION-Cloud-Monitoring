package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class AccountingRule extends AbstractAggregationRule {
    /***/
    private static final Logger log    = LoggerFactory.getLogger(AccountingRule.class);
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<" + this.getClass().getSimpleName() + ", running every " + (period / 1000) + " second(s)>";
    }


    /**
     * @param eventList
     * @return
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
    
    private static class Storlet {
    	private final String storletCodeType;
    	private final String storletType;
    	private long noTriggers = 0;
    	private long sumExecutionTime = 0;

    	/**
    	 * @param storletCodeType
    	 * @param storletType
    	 */
    	public Storlet(String storletCodeType, String storletType) {
			this.storletCodeType = storletCodeType;
			this.storletType = storletType;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((storletCodeType == null) ? 0 : storletCodeType
							.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Storlet other = (Storlet) obj;
			if (storletCodeType == null) {
				if (other.storletCodeType != null)
					return false;
			} else if (!storletCodeType.equals(other.storletCodeType))
				return false;
			return true;
		}
    }
    
    /**
     * 
     */
    private static class TenantStorlets {
    	private final String tenant;
    	private final ArrayList<Storlet> storlets = new ArrayList<AccountingRule.Storlet>();

    	/**
    	 * 
    	 * @param tenant
    	 */
    	public TenantStorlets(String tenant) {
			this.tenant = tenant;
		}
    	
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((tenant == null) ? 0 : tenant.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TenantStorlets other = (TenantStorlets) obj;
			if (tenant == null) {
				if (other.tenant != null)
					return false;
			} else if (!tenant.equals(other.tenant))
				return false;
			return true;
		}
    }

    // {"storletType":"telefonica.container3.Text2SpeechStorletAGDefinition","tenantID":"telefonica","count":1,"originating-service":"SRE","storletCodeType":"inet.hi.telefonica.vision.storlet.textToSpeech.Text2SpeechStorlet","id":"8e332a5b-85f9-4f5d-8da7-2f182ec930d6","timestamp":1349786239648,"objectID":"Text2SpeechStorletAG","end_time":1349786235551,"start_time":1349786197483,"originating-cluster":"test","originating-machine":"10.0.1.101","containerID":"container3"}

    /**
     * @param list
     * @return
     */
    private static ArrayList<HashMap<String, Object>> transformStorletEvents(final ArrayList<Event> list) {
    	final HashMap<String, TenantStorlets> tenants = new HashMap<String, AccountingRule.TenantStorlets>();
    	
    	for (final Event e : list) {
    		final String tenantName = (String) e.get("tenantID");
    		final TenantStorlets entry = tenants.get(tenantName);
			final Storlet s = new Storlet((String)e.get("storletCodeType"), (String)e.get("storletType"));

    		if (entry == null) {
    			final TenantStorlets tenant = new TenantStorlets(tenantName);
    			
    			s.noTriggers = 1;
    			s.sumExecutionTime = (Long)e.get("end_time") - (Long)e.get("start_time");
    			tenant.storlets.add(s);
    			tenants.put(tenantName, tenant);
    		} else {
    			final int ind = entry.storlets.indexOf(s);
    			
    			if (ind < 0) {
    				s.noTriggers = 1;
        			s.sumExecutionTime = (Long)e.get("end_time") - (Long)e.get("start_time");
        			entry.storlets.add(s);
    			} else {
    				++s.noTriggers;
    				s.sumExecutionTime += (Long)e.get("end_time") - (Long)e.get("start_time");
    			}
    		}
    	}
    		
    	// - Tenant
    	// - Total time for the all storlets of this tenant (we SUM ALL the execution times for all the storlets in the whole period)
    	// - For each storlet, the number of executions
    	
    	final ArrayList<HashMap<String, Object>> tenantList = new ArrayList<HashMap<String,Object>>();
    	
    	for (String tenantName : tenants.keySet()) {
    		final HashMap<String, Object> o = new HashMap<String, Object>();
    		final ArrayList<HashMap<String, Object>> storlets = new ArrayList<HashMap<String,Object>>();
    		
    		o.put("tenantID", tenantName);
    		o.put("storlets",storlets);
    		
    		for (final Storlet s : tenants.get(tenantName).storlets) {
    			final HashMap<String, Object> ss = new HashMap<String, Object>();
    			
    			ss.put("storletCodeType", s.storletCodeType);
    			ss.put("storletType", s.storletType);
    			ss.put("executionTime", s.sumExecutionTime);
    			ss.put("count", s.noTriggers);

    			storlets.add(ss);
    		}
    		
    		tenantList.add(o);
    	}
    	
    	return tenantList;
	}
    

	/**
     * @param list
     * @param operation
     * @return
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
     * @param eventList
     * @return the list of write events as prescribed by accounting.
     */
    private static ArrayList<HashMap<String, Object>> transformWriteList(final ArrayList<Event> eventList) {
        return transformByOperation(eventList, "write");
    }
}
