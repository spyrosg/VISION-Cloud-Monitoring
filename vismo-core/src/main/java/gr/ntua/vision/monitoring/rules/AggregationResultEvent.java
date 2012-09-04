package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.Event;

public interface AggregationResultEvent extends Event {
	long tStart();

	long tEnd();
}
