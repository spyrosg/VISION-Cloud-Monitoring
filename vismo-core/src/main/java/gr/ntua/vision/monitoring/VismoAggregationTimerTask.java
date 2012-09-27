package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.scheduling.VismoRepeatedTask;
import gr.ntua.vision.monitoring.sinks.EventSink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class VismoAggregationTimerTask extends VismoRepeatedTask implements EventSourceListener {
    /***/
    private static final Logger log = LoggerFactory.getLogger(VismoAggregationTimerTask.class);
    /***/
    private final RuleList      rules;
    /***/
    private final EventSink     sink;


    /**
     * Constructor.
     * 
     * @param rules
     * @param sink
     */
    public VismoAggregationTimerTask(final RuleList rules, final EventSink sink) {
        this.rules = rules;
        this.sink = sink;
    }


    /**
     * @return the period.
     */
    @Override
    public long getPeriod() {
        return rules.getPeriod();
    }


    /**
     * @param e
     */
    @Override
    public void receive(final Event e) {
        rules.matchToEvent(e);
    }


    /**
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        final long periodInSeconds = rules.getPeriod() / 1000;

        log.trace("timer of {} seconds expired, starting aggregation", periodInSeconds);

        final long start = System.currentTimeMillis();

        try {
            final long aggregationPeriodTimestamp = scheduledExecutionTime() - rules.getPeriod();

            rules.runRules(aggregationPeriodTimestamp, sink);
        } catch (final Throwable x) {
            log.error("performPendingOperations exception: ", x);
        }

        log.trace("aggregation end for {} seconds timer, in {} seconds", periodInSeconds,
                  (System.currentTimeMillis() - start) / 1000.0);
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<VismoAggregationTimerTask: expiring every " + (getPeriod() / 1000) + " second(s)>";
    }
}
