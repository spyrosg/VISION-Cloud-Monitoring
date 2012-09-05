package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.ArrayList;
import java.util.HashMap;
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
	private final String aggregationField;
	/***/
	private final String newField;
	/***/
	private final String operation;
	/***/
	private static final Logger log = LoggerFactory.getLogger(CTORule.class);
	/***/
	private static final String SPECIAL_FIELD = "transaction-duration";
	/***/
	private static final long MIN = 60 * 1000;

	public class ContainerRep {
		/***/
		public final String tenant;
		/***/
		public final String name;

		public ContainerRep(String tenant, String name) {
			this.tenant = tenant;
			this.name = name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((tenant == null) ? 0 : tenant.hashCode());
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
			ContainerRep other = (ContainerRep) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
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

		private CTORule getOuterType() {
			return CTORule.this;
		}
	}

	/**
	 * @param aggregationField
	 * @param resultField
	 */
	public CTORule(final String operation, String aggregationField, final String resultField) {
		this.operation = operation;
		this.aggregationField = aggregationField;
		this.newField = resultField;
	}

	/**
	 * @param eventList
	 */
	private void aggregateContainerSize(final Map<ContainerRep, Long> containersSize, List<? extends Event> eventList) {
		for (final Event e : eventList)
			try {
				final String tenant = (String) e.get("tenant");
				final String name = (String) e.get("container");
				final Long size = getLongValue(e, aggregationField);
				final ContainerRep c = new ContainerRep(tenant, name);

				if (size == null) {
					log.trace("event with no appropriate field '{}'; skipping", aggregationField);
					continue;
				}

				if (containersSize.containsKey(c)) {
					final long oldSize = containersSize.get(c);

					containersSize.put(c, oldSize + size);
				} else {
					containersSize.put(c, size);
				}
			} catch (Throwable x) {
				x.printStackTrace();
				log.error("continuing aggregation", x);
			}
	}

	private static Long getLongValue(final Event e, final String field) {
		final Object val = e.get(field);

		if (val == null)
			return null;

		if (val instanceof String)
			return Long.valueOf((String) val);

		try {
			return (Long) val;
		} catch (ClassCastException x) {
			log.trace("expecting field '{}' of type {} ...", field, Long.class);
			log.trace("but got value {} of type {}", val, val.getClass());
			log.trace("", x);

			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public AggregationResultEvent aggregate(final long aggregationStartTime, List<? extends Event> eventList) {
		final Map<ContainerRep, Long> containersSize = new HashMap<ContainerRep, Long>();

		aggregateContainerSize(containersSize, eventList);

		@SuppressWarnings("rawtypes")
		final Map dict = appendNewFields(eventList, aggregationStartTime, containersSize);

		return new VismoAggregationResultEvent(dict);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map appendNewFields(List<? extends Event> eventList, final long aggregationStartTime,
			final Map<ContainerRep, Long> containersSize) {
		final Event lastEvent = eventList.get(eventList.size() - 1);
		final Map dict = (Map) lastEvent.get(DICT);

		// FIXME: these should be gotten off the timer
		dict.put("tStart", aggregationStartTime - MIN); // FIXME: bind this to the timer
		dict.put("tEnd", aggregationStartTime);
		dict.put(newField, getContainersList(containersSize));
		dict.put("topic", TOPIC);

		return dict;
	}

	private List<Object> getContainersList(final Map<ContainerRep, Long> containersSize) {
		final List<Object> containers = new ArrayList<Object>();

		for (final ContainerRep c : containersSize.keySet()) {
			final Map<String, Object> o = new HashMap<String, Object>();

			o.put("tenant", c.tenant);
			o.put("container", c.name);
			o.put("size", containersSize.get(c));
			containers.add(o);
		}

		return containers;
	}

	@Override
	public boolean matches(Event e) {
		final String op = (String) e.get("operation");

		// FIXME: add a field for events coming from vismo_dispatch
		return e.get(SPECIAL_FIELD) != null && op.equals(operation);
	}

	@Override
	public boolean hasExpired() {
		// TODO????
		return true;
	}

	@Override
	public String toString() {
		return "#<" + this.getClass().getSimpleName() + "[" + operation + "] on field: " + aggregationField
				+ ", with new field '" + newField + "'>";
	}
}
