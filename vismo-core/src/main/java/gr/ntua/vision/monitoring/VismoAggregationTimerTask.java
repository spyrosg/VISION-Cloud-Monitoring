package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is SO GONNA DIE after the f2f.
 */
public class VismoAggregationTimerTask extends TimerTask implements EventListener {
    /***/
    private static final Logger    log = LoggerFactory.getLogger(VismoAggregationTimerTask.class);
    /***/
    private final EventDistributor distributor;
    /***/
    private final RuleList         rules;


    /**
     * Constructor.
     * 
     * @param distributor
     * @param rules
     */
    public VismoAggregationTimerTask(final EventDistributor distributor, final RuleList rules) {
        this.distributor = distributor;
        this.rules = rules;
    }


    /**
     * @return the period.
     */
    public long getPeriod() {
        return rules.getPeriod();
    }


    /**
     * @see gr.ntua.vision.monitoring.EventListener#notify(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void notify(final Event e) {
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

            rules.runRules(aggregationPeriodTimestamp, distributor);
        } catch (final Throwable x) {
            log.trace("performPendingOperations exception: ", x);
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
