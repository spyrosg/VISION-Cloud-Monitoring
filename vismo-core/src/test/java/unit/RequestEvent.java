package unit;

import gr.ntua.vision.monitoring.events.Event;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class RequestEvent implements Event {
	private final String topic;
	private long size;

	public RequestEvent(String topic, long size) {
		this.topic = topic;
		this.size = size;
	}

	@Override
	public Object get(String key) {
		if (key.equals("content-size"))
			return size;

		return null;
	}

	@Override
	public InetAddress originatingIP() throws UnknownHostException {
		return InetAddress.getLocalHost();
	}

	@Override
	public String originatingService() {
		return "object-service";
	}

	@Override
	public long timestamp() {
		return 0;
	}

	@Override
	public String topic() {
		return topic;
	}
}
