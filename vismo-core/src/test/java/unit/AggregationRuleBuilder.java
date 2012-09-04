package unit;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.rules.AggregationResultEvent;
import gr.ntua.vision.monitoring.rules.AggregationRule;

import java.util.List;

public class AggregationRuleBuilder {
	public AggregationRule aggregationOnField(final String field) {
		return new AggregationRule() {
			@Override
			public AggregationResultEvent aggregate(List<? extends Event> eventList) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean matches(Event e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean hasExpired() {
				// TODO Auto-generated method stub
				return false;
			}
		};
	}
}
