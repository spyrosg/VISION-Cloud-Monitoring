package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 * THis is just a shortcut to {@link RuleProc} tailored to {@link MonitoringEvent}s.
 */
public interface VismoRule extends RuleProc<MonitoringEvent> {
    // NOP
}
