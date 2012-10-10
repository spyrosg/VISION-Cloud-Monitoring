package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.sinks.EventSink;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

/**
 * 
 */
public class SLAPerRequestChannel implements EventSourceListener {
	/***/
	private static final String SPECIAL_FIELD = "transaction-duration";
	/***/
	private final EventSink sink;

	/**
	 * Constructor.
	 * 
	 * @param sink
	 */
	public SLAPerRequestChannel(final EventSink sink) {
		this.sink = sink;
	}

	/**
	 *
	 */
	private static class SLAEvent implements Event {
		private final Event e;
		/***/
		private static final String topic = "sla-per-request";
		/***/
		private final String id = UUID.randomUUID().toString();

		/**
		 * Constructor.
		 * 
		 * @param e
		 */
		public SLAEvent(Event e) {
			this.e = e;
		}

		@Override
		public Object get(String key) {
			if (key.equals("id"))
				return id;

			return e.get(key);
		}

		@Override
		public InetAddress originatingIP() throws UnknownHostException {
			return e.originatingIP();
		}

		@Override
		public String originatingService() {
			return e.originatingService();
		}

		@Override
		public long timestamp() {
			return e.timestamp();
		}

		@Override
		public String topic() {
			return topic;
		}

	}

	/**
	 * @see gr.ntua.vision.monitoring.EventSourceListener#receive(gr.ntua.vision.monitoring.events.Event)
	 */
	@Override
	public void receive(final Event e) {
		if (!isCompleteObsEvent(e))
			return;

		sink.send(new SLAEvent(e));
	}

	/**
	 * Is this a complete object service event? Since we receive all events from
	 * object service, some of them are incomplete, in the sense that contain
	 * parts of the request/response cycle.
	 * 
	 * @param e
	 *            the event.
	 * @return <code>true</code> iff the
	 */
	private static boolean isCompleteObsEvent(final Event e) {
		return e.get(SPECIAL_FIELD) != null;
	}
}
