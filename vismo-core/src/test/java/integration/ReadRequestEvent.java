package integration;

import unit.RequestEvent;

public class ReadRequestEvent extends RequestEvent {
	public ReadRequestEvent(final long size) {
		super("reads", size);
	}
}
