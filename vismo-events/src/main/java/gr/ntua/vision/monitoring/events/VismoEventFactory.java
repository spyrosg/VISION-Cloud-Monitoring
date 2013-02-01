package gr.ntua.vision.monitoring.events;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * This is used to deserialize events received from the wire, to actual java objects of type {@link MonitoringEvent}.
 */
public class VismoEventFactory implements EventFactory {
    /** the log target. */
    private static final Logger log    = Logger.getLogger(VismoEventFactory.class.getName());
    /** the JSON parser. */
    private final JSONParser    parser = new JSONParser();


    /**
     * @see gr.ntua.vision.monitoring.events.EventFactory#createEvent(java.lang.String)
     */
    @Override
    public MonitoringEvent createEvent(final String str) {
        final Map<String, Object> dict = parse(str);

        return dict != null ? new VismoEvent(dict) : null;
    }


    /**
     * Deserialize the message as a json object.
     * 
     * @param msg
     *            the message string.
     * @return if successful, return a java {@link Map} representing the json object, <code>null</code> otherwise.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parse(final String msg) {
        try {
            return (Map<String, Object>) parser.parse(msg);
        } catch (final ParseException e) {
            log.severe("error deserializing: " + msg);
            log.log(Level.SEVERE, "ParseException", e);

            return null;
        }
    }
}
