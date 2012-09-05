package integration;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.AggregationPerContainerRule;
import gr.ntua.vision.monitoring.rules.AggregationResultEvent;
import gr.ntua.vision.monitoring.rules.AggregationRule;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

public class AggregationTest {
	/***/
	@Ignore("all aggregation result events should be based on previous events, these are not")
	@Test
	public void calculateNoOfReadBytesPerUserInLastPeriod() throws Exception {
		final List<? extends Event> readList = Arrays.asList(new ReadRequestEvent(1000), new ReadRequestEvent(1000),
				new ReadRequestEvent(1000));
		final AggregationRule additionOnContentSize = new AggregationPerContainerRule("GET", "content-size", "size");
		final AggregationResultEvent result = additionOnContentSize.aggregate(readList);

		assertEquals(result.get("size"), 3000.0);
	}

	/***/
	private static Event getEvent(final Map<String, Object> dict) {
		return new Event() {
			@Override
			public String topic() {
				return "reads";
			}

			@Override
			public long timestamp() {
				return 0;
			}

			@Override
			public String originatingService() {
				return "test";
			}

			@Override
			public InetAddress originatingIP() throws UnknownHostException {
				return InetAddress.getLocalHost();
			}

			@Override
			public Object get(String key) {
				return dict.get(key);
			}
		};
	}
}
