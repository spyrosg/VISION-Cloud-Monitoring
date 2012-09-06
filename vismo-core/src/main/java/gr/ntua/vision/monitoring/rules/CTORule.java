package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: sum of content-size and # of accesses per tenant and container
// TODO: sum and # of think-times per tenant and container
// TODO: sum and # of rethink-times per tenant and container
public class CTORule implements AggregationRule {
	private static final String TOPIC = "CTO";
	/***/
	private static final String DICT = "!dict";
	/***/
	private final String operation;
	/***/
	private static final Logger log = LoggerFactory.getLogger(CTORule.class);
	/***/
	private static final String SPECIAL_FIELD = "transaction-duration";
	/***/
	private static final String AGGREGATION_FIELD = "content-size";

	/**
	 * @param aggregationField
	 * @param resultField
	 */
	public CTORule(final String operation) {
		this.operation = operation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AggregationResultEvent aggregate(final long aggregationStartTime, List<? extends Event> eventList) {
		@SuppressWarnings("rawtypes")
		final Map dict = getFinalObject(eventList, aggregationStartTime);
		aggregateThinkTime(eventList);

		return new VismoAggregationResultEvent(dict);
	}

	@Override
	public boolean matches(Event e) {
		final String op = (String) e.get("operation");

		// FIXME: add a field for events coming from vismo_dispatch
		return e.get(SPECIAL_FIELD) != null && op.equals(operation);
	}

	@Override
	public String toString() {
		return "#<" + this.getClass().getSimpleName() + "[" + operation + "]>";
	}

	/**
	 * First pass. Put all the different access sizes on a list.
	 * 
	 * @param eventList
	 * @return
	 */
	private ArrayList<Container> getPerTenantPerContainerContentSize(List<? extends Event> eventList) {
		final ArrayList<Container> containersList = new ArrayList<Container>();

		for (final Event e : eventList) {
			try {
				final Long size = getFieldValueAsLong(e, AGGREGATION_FIELD);

				// no content-size field provided
				if (size == null)
					continue;

				final String tenantName = (String) e.get("tenant");
				final String containerName = (String) e.get("container");

				containersList.add(new Container(tenantName, containerName, size));
			} catch (final Throwable x) {
				x.printStackTrace();
				log.error("continuing aggregation", x);
			}
		}

		return containersList;
	}

	/**
	 * Second pass. Aggregate all container sizes per tenant. This removes duplicate containers, unifying them in just one, with
	 * size, the total size of all containers with the same name and tenant.
	 * 
	 * @param containers
	 * @return
	 */
	private ArrayList<Container> sumContentSizePerContainer(List<Container> containers) {
		final HashMap<Container, Long> aggregatedContentSize = new HashMap<Container, Long>();

		for (final Container c : containers) {
			final Long oldSize = aggregatedContentSize.remove(c);

			if (oldSize == null) // first time we see this container
				aggregatedContentSize.put(c, c.size);
			else
				aggregatedContentSize.put(c, oldSize + c.size);
		}

		return new ArrayList<Container>(aggregatedContentSize.keySet());
	}

	private HashMap<String, Object> getFinalObject(List<? extends Event> eventList, final long aggregationStartTime) {
		final ArrayList<Map<String, Object>> tenantList = prepareTenantsList(eventList);
		final ArrayList<Container> firstPass = getPerTenantPerContainerContentSize(eventList);
		final ArrayList<Container> secondpass = sumContentSizePerContainer(firstPass);

		for (final Map<String, Object> tenant : tenantList) {
			@SuppressWarnings("unchecked")
			final ArrayList<Map<String, Object>> containers = (ArrayList<Map<String, Object>>) tenant.get("containers");

			for (final Container c : secondpass) {
				final HashMap<String, Object> container = new HashMap<String, Object>();

				container.put("name", c.name);
				container.put("size-sum", c.size);

				containers.add(container);
			}

			final HashMap<String, Object> t = new HashMap<String, Object>();

			t.put("name", tenant.get("name"));
			t.put("containers", containers);
		}

		@SuppressWarnings("unchecked")
		final HashMap<String, Object> dict = (HashMap<String, Object>) getBaseEvent(eventList).get(DICT);

		dict.put("tenants", tenantList);
		dict.put("topic", TOPIC);
		dict.put("tStart", aggregationStartTime);
		dict.put("tEnd", System.currentTimeMillis());

		return dict;
	}

	private ArrayList<Map<String, Object>> prepareTenantsList(final List<? extends Event> eventList) {
		final ArrayList<Map<String, Object>> tenantList = new ArrayList<Map<String, Object>>();

		for (final Event e : eventList) {
			final HashMap<String, Object> o = new HashMap<String, Object>();

			o.put("name", e.get("tenant"));
			o.put("containers", new ArrayList<Map<String, Object>>());
			tenantList.add(o);
		}

		return tenantList;
	}

	private static Event getBaseEvent(List<? extends Event> eventList) {
		return eventList.get(0);
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

	/* SPYROS */

	private void aggregateThinkTime(List<? extends Event> eventList) {
		// sort list
		Collections.sort(eventList, new TimestampComparator());

		// create user list
		List<User> userList = new ArrayList<User>();

		for (final Event e : eventList)
			try {
				// final String tenant = (String) e.get("tenant");
				// final String name = (String) e.get("container");
				// final Long size = getLongValue(e, aggregationField);
				final String tenant = (String) e.get("tenant");
				final String user = (String) e.get("user");
				// final String userTenant = userName+"@"+tenant;
				final String userName = user + "@" + tenant;
				final Long timestamp = (Long) e.get("timestamp");
				boolean foundUser = false;

				if (userList.isEmpty()) {
					userList.add(new User(userName, timestamp));
				} else {

					Iterator<User> iterator = userList.iterator();

					while (iterator.hasNext()) {
						User tmp = iterator.next();
						if (tmp.name.equals(userName)) {

							if (!tmp.timeList.isEmpty())
								tmp.differences.add(timestamp - tmp.timeList.get(tmp.timeList.size() - 1));

							tmp.timeList.add(timestamp);
							foundUser = true;
							break;

						}

					}

					if (!foundUser)
						userList.add(new User(userName, timestamp));
				}
			} catch (Throwable x) {
				x.printStackTrace();
				log.error("continuing aggregation (think time exploded", x);
			}

		Iterator<User> userIterator = userList.iterator();

		while (userIterator.hasNext()) {
			User tmp = userIterator.next();
			Iterator<Long> differencesIterator = tmp.differences.iterator();
			while (differencesIterator.hasNext()) {
				tmp.thinkTime = tmp.thinkTime + differencesIterator.next();
				tmp.count++;
			}

		}

		printList(userList);
		// final ArrayList<Map<String, Object>>
		// final HashMap<String, Object> u = new HashMap<String, Object>();

	}

	private void printList(List<User> userList) {
		Iterator<User> userIterator = userList.iterator();
		while (userIterator.hasNext()) {
			User tmp = userIterator.next();
			System.out.println("Name= " + tmp.name + " Count= " + tmp.count + " Thinktime= " + tmp.thinkTime);

		}

	}

	private class User {
		String name;
		ArrayList<Long> timeList = new ArrayList();
		ArrayList<Long> differences = new ArrayList();
		Long thinkTime = 0L;
		int count = 0;

		public User(String name, long timeStamp) {
			this.name = name;
			this.timeList.add(timeStamp);
		}

		public void setTimeList(ArrayList<Long> timeList) {
			this.timeList = timeList;
		}

		public ArrayList<Long> getTimeList() {
			return timeList;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ArrayList<Long> getDifferences() {
			return differences;
		}

		public void setDifferences(ArrayList<Long> differences) {
			this.differences = differences;
		}

	}

	public class TimestampComparator implements Comparator<Event> {
		@Override
		public int compare(Event e1, Event e2) {
			Long time1 = (Long) e1.get("timestamp");
			Long time2 = (Long) e2.get("timestamp");

			return time1.compareTo(time2);
		}
	}
}
