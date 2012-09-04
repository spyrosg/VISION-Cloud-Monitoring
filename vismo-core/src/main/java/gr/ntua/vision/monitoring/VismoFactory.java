package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.rules.AdditionRule;
import gr.ntua.vision.monitoring.rules.AggregationRule;
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
	/** the configuration object. */
	private final VismoConfiguration conf;
	private final List<AggregationRule> ruleList = new ArrayList<AggregationRule>();
	/***/
	private static final Logger log = LoggerFactory.getLogger(VismoFactory.class);
	/***/
	private static final Timer timer = new Timer();

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

		registerRule(new AdditionRule("content-size", "size"));

		// receiver.subscribe(new EventDistributor(zmq.newBoundPubSocket(conf.getConsumersPoint())));
		final VismoAggregationController ruleTimer = new VismoAggregationController(ruleList, zmq.newBoundPubSocket(conf
				.getConsumersPoint()));

		timer.schedule(ruleTimer, 0, 60 * 1000);

		receiver.subscribe(ruleTimer);
		vismo.addTask(new UDPFactory(conf.getUDPPort()).buildServer(vismo));
		vismo.addTask(receiver);

		return vismo;
	}
}
