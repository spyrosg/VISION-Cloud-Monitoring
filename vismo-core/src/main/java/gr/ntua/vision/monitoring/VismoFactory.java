package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.rules.AggregationRule;
import gr.ntua.vision.monitoring.rules.CTORule;
import gr.ntua.vision.monitoring.udp.UDPFactory;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;

/**
 * This is used to configure and properly initialize the application graph.
 */
public class VismoFactory {
	private static final long EVERY_MIN = 60 * 1000;
	/***/
	private static final long AFTER_TEN_SECONDS = 10 * 1000;
	/** the configuration object. */
	private final VismoConfiguration conf;
	/***/
	private final List<AggregationRule> ruleList = new ArrayList<AggregationRule>();
	/***/
	private static final Logger log = LoggerFactory.getLogger(VismoFactory.class);
	/***/
	private static final Timer timer = new Timer();
	/***/
	private static final String[] operations = { "GET", "PUT", "DELETE" };

	/**
	 * Constructor.
	 * 
	 * @param conf
	 *            the configuration object.
	 */
	public VismoFactory(final VismoConfiguration conf) {
		this.conf = conf;
	}

	private void registerRule(final AggregationRule rule) {
		log.trace("registering rule: {}", rule);
		ruleList.add(rule);
	}

	/**
	 * Configure and return a {@link Vismo} instance.
	 * 
	 * @param listeners
	 * @return a new vismo instance.
	 * @throws SocketException
	 */
	public Vismo build(final EventListener... listeners) throws SocketException {
		final ZMQSockets zmq = new ZMQSockets(new ZContext());
		final Vismo vismo = new Vismo(new VismoVMInfo());

		final LocalEventsCollector receiver = new LocalEventsCollectorFactory(conf).build(zmq);

		for (final EventListener listener : listeners)
			receiver.subscribe(listener);

		registerRule(new CTORule("GET", "content-size", "containers"));
		registerRule(new CTORule("PUT", "content-size", "containers"));

		// for (final String op : operations)
		// registerRule(new AggregationOnNumberOfRequests(op, "count"));

		final EventDistributor stuff = new EventDistributor(zmq.newBoundPubSocket(conf.getConsumersPoint()));
		final VismoAggregationController ruleTimer = new VismoAggregationController(stuff, ruleList);

		timer.schedule(ruleTimer, AFTER_TEN_SECONDS, EVERY_MIN);

		receiver.subscribe(ruleTimer);
		vismo.addTask(new UDPFactory(conf.getUDPPort()).buildServer(vismo));
		vismo.addTask(receiver);

		return vismo;
	}
}
