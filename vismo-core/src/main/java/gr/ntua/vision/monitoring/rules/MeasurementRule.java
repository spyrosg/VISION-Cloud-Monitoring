package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This is used to take an <code>Object Service</code> generated event and passed it down with a default <code>topic</code>.
 */
public class MeasurementRule extends Rule {
    /***/
    static final Pattern        ID_PATTERN    = Pattern.compile("\"id\":\"[^\"]+\"");
    /***/
    static final Pattern        TOPIC_PATTERN = Pattern.compile("\"topic\"\\s*:\\s*\"([^\"]+)\"");
    /***/
    private static final String OBS           = "object_service";
    /***/
    private static final String TOPIC         = "measurement";


    /**
     * Constructor.
     * 
     * @param engine
     */
    public MeasurementRule(final VismoRulesEngine engine) {
        super(engine);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RuleProc#performWith(java.lang.Object)
     */
    @Override
    public void performWith(final MonitoringEvent e) {
        final String id = UUID.randomUUID().toString();

        if (OBS.equals(e.originatingService()))
            send(new MonitoringEvent() {
                @Override
                public Object get(final String key) {
                    if ("topic".equals(key))
                        return TOPIC;
                    if ("id".equals("id"))
                        return id;

                    return e.get(key);
                }


                @Override
                public InetAddress originatingIP() throws UnknownHostException {
                    return e.originatingIP();
                }


                @Override
                public String originatingService() {
                    return e.originatingService();
                }


                @Override
                public String serialize() {
                    final String serialized = e.serialize();
                    final Matcher m = TOPIC_PATTERN.matcher(serialized);
                    final Matcher m1 = ID_PATTERN.matcher(serialized);
                    final String tmp;

                    if (m1.find())
                        tmp = m1.replaceFirst("\"id\":\"" + id + "\"");
                    else
                        tmp = serialized;

                    m.replaceFirst("\"id\":\"");

                    if (m.find() && m.groupCount() == 1)
                        return m.replaceAll(String.format("\"topic\": \"%s\"", TOPIC));

                    final int idx = tmp.lastIndexOf("}");

                    if (idx < 0)
                        return tmp;

                    final String orig = tmp.substring(0, idx);

                    return String.format("%s,\"topic\":\"%s\"}", orig, TOPIC);
                }


                @Override
                public long timestamp() {
                    return e.timestamp();
                }


                @Override
                public String topic() {
                    return TOPIC;
                }
            });
    }
}
