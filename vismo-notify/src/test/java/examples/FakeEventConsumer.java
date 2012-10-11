package examples;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.notify.EventHandler;
import gr.ntua.vision.monitoring.notify.EventRegistry;

import java.util.Map;

/**
 *
 */
public class FakeEventConsumer {
	private static final String SPECIAL_FIELD = "transaction-throughput";

	private static class SAPLogger implements EventHandler {
		/**
		 * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.Event)
		 */
		@Override
		public void handle(final Event e) {
			try {
				@SuppressWarnings("rawtypes")
				final Map dict = (Map) e.get("!dict");
				final String tenant = (String) e.get("tenant");
				final Object special = e.get(SPECIAL_FIELD);

				if (special != null && tenant != null && tenant.equalsIgnoreCase("sla_SAPIBI")) {
					final String user = (String) e.get("user");

					System.out.println(getClass().getSimpleName() + ": " + e.get("originating-machine") + " => " + dict);
				}
			} catch (final Throwable x) {
				x.printStackTrace();
			}
		}
	}

	/**
     *
     */
	public static class LoggingHandler implements EventHandler {
		/**
		 * @see gr.ntua.vision.monitoring.notify.EventHandler#handle(gr.ntua.vision.monitoring.events.Event)
		 */
		@Override
		public void handle(final Event e) {
			try {
				@SuppressWarnings("rawtypes")
				final Map dict = (Map) e.get("!dict");
				final Object special = e.get(SPECIAL_FIELD);

				if (special != null)
					System.err.println(getClass().getSimpleName() + ": " + e.get("originating-machine") + " => " + dict);
			} catch (final Throwable x) {
				x.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(final String... args) {
		final EventRegistry registry = new EventRegistry("tcp://10.0.1.103:56430");

		registry.registerToAll(new SAPLogger());
		registry.registerToAll(new LoggingHandler());
	}
}
