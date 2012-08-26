package gr.ntua.vision.monitoring.events;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * This is used to de-serialize events received from the wire to actual java objects of type {@link Event}.
 */
public class VismoEventFactory {
    /** the log target. */
    private static final Logger log    = Logger.getLogger(VismoEventFactory.class.getName());
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

        return dict != null ? new VismoEvent(dict) : null;
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
            log.severe("error deserializing: " + msg);
            log.log(Level.SEVERE, "ParseException", e);

            return null;
        }
    }
}
