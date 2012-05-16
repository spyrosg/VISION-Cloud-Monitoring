package gr.ntua.vision.monitoring.events;

import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to de-serialize strings to {@link Event} objects and also hide the {@link Event} implementations from the rest of
 * the system.
 */
public class EventFactory {
    /** the log target. */
    private static final Logger log    = LoggerFactory.getLogger(EventFactory.class);
    /** the JSON parser. */
    private final JSONParser    parser = new JSONParser();


    /**
     * Try to construct an event from the given string.
     * 
     * @param str
     *            the string to de-serialize.
     * @return on success, a new {@link Event}, <code>null</code> otherwise.
     */
    public Event createEvent(final String str) {
        @SuppressWarnings("rawtypes")
        final Map dict = parse(str);

        return dict != null ? new DummyEvent(dict) : null;
    }


    /**
     * De-serialize the message as a json object.
     * 
     * @param msg
     *            the message string.
     * @return if successful, return a java {@link Map} representing the json object, <code>null</code> otherwise.
     */
    @SuppressWarnings("rawtypes")
    private Map parse(final String msg) {
        try {
            return (Map) parser.parse(msg);
        } catch (final ParseException e) {
            log.error("error deserializing: {}", msg);
            log.error("ParseException", e);

            return null;
        }
    }
}
