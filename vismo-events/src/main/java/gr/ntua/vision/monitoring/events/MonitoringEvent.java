package gr.ntua.vision.monitoring.events;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * This is the interface implemented by all <em>vismo</em> specific events. In abstract terms, an event is just a collection of
 * key/value pairs. The keys are of type {@link String} and the values are of type {@link Object} - that is we cannot, in general,
 * make any type guarantees of the values found in the event; or that the key/value will even be found in the event. However,
 * there do exist some well known fields, fields that are expected to be found in every event and whose corresponding value
 * returns a non <code>null</code> value; a method convenience is provided for these fields.
 */
public interface MonitoringEvent {
    /**
     * Return the value under given key.
     * 
     * @param key
     *            the key.
     * @return the corresponding value, or <code>null</code> if the key cannot be found in the event.
     */
    Object get(String key);


    /**
     * @return the ip address of the service <code>this</code> event originated from.
     * @throws UnknownHostException
     *             when the address cannot be properly decoded.
     */
    InetAddress originatingIP() throws UnknownHostException;


    /**
     * @return the name of the service <code>this</code> event originated from.
     */
    String originatingService();


    /**
     * @return the instant of time <code>this</code> event was generated, in the <strong>originating</strong> host. The time is
     *         measured in milliseconds since the epoch.
     */
    long timestamp();


    /**
     * @return the topic <code>this</code> event corresponds to.
     */
    String topic();
}
