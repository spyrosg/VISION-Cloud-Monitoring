package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.CTORule;
import gr.ntua.vision.monitoring.udp.UDPFactory;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import java.net.SocketException;
import java.util.Timer;

import org.zeromq.ZContext;


/**
 * This is used to configure and properly initialize the application graph.
 */
public class VismoFactory {
    /***/
    private static final long        AFTER_TEN_SECONDS   = 10 * 1000;
    /***/
    private static final long        EVERY_MINUTE        = 60 * 1000;
    /***/
    private static final long        EVERY_THREE_SECONDS = 3 * 1000;
    /***/
    private static final String[]    operations          = { "GET", "PUT", "DELETE" };
    /***/
    private static final Timer       timer               = new Timer();
    /** the configuration object. */
    private final VismoConfiguration conf;


    /**
     * Constructor.
     * 
     * @param conf
     *            the configuration object.
     */
    public VismoFactory(final VismoConfiguration conf) {
        this.conf = conf;
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
        final EventDistributor distributor = new EventDistributor(zmq.newBoundPubSocket(conf.getConsumersPoint()));

        for (final EventListener listener : listeners)
            receiver.subscribe(listener);

        addPassThroughListener(receiver, distributor);

        // TODO: move rules registration and timers to a file

        final RuleList everyThreeSeconds = new RuleList(EVERY_THREE_SECONDS);
        everyThreeSeconds.registerRule(new CTORule("cto-3-sec", EVERY_THREE_SECONDS));

        final RuleList everyMinute = new RuleList(EVERY_MINUTE);
        everyMinute.registerRule(new CTORule("cto-1-min", EVERY_MINUTE));

        registerRuleTimerTask(receiver, getTimerFor(distributor, everyThreeSeconds));
        registerRuleTimerTask(receiver, getTimerFor(distributor, everyMinute));

        vismo.addTask(new UDPFactory(conf.getUDPPort()).buildServer(vismo));
        vismo.addTask(receiver);

        return vismo;
    }


    /**
     * @param receiver
     * @param distributor
     */
    private static void addPassThroughListener(final LocalEventsCollector receiver, final EventDistributor distributor) {
        receiver.subscribe(new EventListener() {
            @Override
            public void notify(final Event e) {
                distributor.serialize(e);
            }
        });
    }


    /**
     * @param distributor
     * @param list
     * @return
     */
    private static VismoAggregationTimerTask getTimerFor(final EventDistributor distributor, final RuleList list) {
        return new VismoAggregationTimerTask(distributor, list);
    }


    /**
     * @param receiver
     * @param ruleTimer
     */
    private static void registerRuleTimerTask(final LocalEventsCollector receiver, final VismoAggregationTimerTask ruleTimer) {
        receiver.subscribe(ruleTimer);
        timer.schedule(ruleTimer, AFTER_TEN_SECONDS, ruleTimer.getPeriod());
    }
}
