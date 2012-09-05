package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggregationPerContainerRule implements AggregationRule {
	/***/
	private static final String DICT = "!dict";
	/***/
	private final String aggregationField;
	/***/
	private final String newField;
	/***/
	private final String operation;
	/***/
	private static final Logger log = LoggerFactory.getLogger(AggregationPerContainerRule.class);
	/***/
	private static final String SPECIAL_FIELD = "transaction-duration";
	/***/
	private final Map<ContainerRep, Long> containersSize = new HashMap<ContainerRep, Long>();

	public class ContainerRep {
		/***/
		private final String tenant;
		/***/
		private final String name;

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

		private AggregationPerContainerRule getOuterType() {
			return AggregationPerContainerRule.this;
		}
	}

	/**
	 * @param aggregationField
	 * @param resultField
	 */
	public AggregationPerContainerRule(final String operation, String aggregationField, final String resultField) {
		this.operation = operation;
		this.aggregationField = aggregationField;
		this.newField = resultField;
	}

	/**
	 * @param eventList
	 */
	private void aggregateContainerSize(List<? extends Event> eventList) {
		for (final Event e : eventList)
			try {
				final String tenant = (String) e.get("tenant");
				final String name = (String) e.get("container");
				final Long size = getValue(e, aggregationField);
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

	private static Long getValue(final Event e, final String field) {
		final Object val = e.get(field);

		if (val == null)
			return null;

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
	public AggregationResultEvent aggregate(List<? extends Event> eventList) {
		double sum = 0;

		// TODO: can we have the events sorted by timestamp? What guarantees do we have?
		// TODO: sort events by timestamp
		for (final Event e : eventList) {
			log.trace("aggregating event for {}", e.getClass());

			final Object val = e.get(aggregationField);

			if (val == null) {
				log.trace("event with no appropriate field '{}'; skipping", aggregationField);
				continue;
			}

			try {
				sum += (Long) val;
			} catch (ClassCastException x) {
				log.trace("expecting field '{}' of type {} ...", aggregationField, Long.class);
				log.trace("but got value {} of type {}", val, val.getClass());
				log.trace("", x);
			}
		}

		return new VismoAggregationResultEvent(appendNewField(eventList, sum));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map appendNewField(List<? extends Event> eventList, double sum) {
		final Event firstEvent = eventList.get(0);
		final Event lastEvent = eventList.get(eventList.size() - 1);
		final Map dict = (Map) lastEvent.get(DICT);

		// FIXME: these should be gotten off the timer
		dict.put("tStart", firstEvent.timestamp());
		dict.put("tEnd", lastEvent.timestamp());
		dict.put(newField, sum);
		dict.put("objects", getObjectList(eventList));

		return dict;
	}

	private Object getObjectList(List<? extends Event> eventList) {
		final List<String> l = new ArrayList<String>(eventList.size());

		for (final Event e : eventList)
			l.add((String) e.get("object"));

		return l;
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
		return "#<AggregationOnContentSizeRule[" + operation + "] on field: " + aggregationField + ", with new field '"
				+ newField + "'>";
	}
}
