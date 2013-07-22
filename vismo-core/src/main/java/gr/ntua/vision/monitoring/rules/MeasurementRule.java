package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This is used to take an <code>Object Service</code> generated event and passed it down with a default <code>topic</code>.
 */
public class MeasurementRule extends Rule {
    /***/
    static final Pattern        p     = Pattern.compile("\"topic\"\\s*:\\s*\"([^\"]+)\"");
    /***/
    private static final String OBS   = "object_service";
    /***/
    private static final String TOPIC = "measurement";


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
        if (OBS.equals(e.originatingService()))
            send(new MonitoringEvent() {
                @Override
                public Object get(final String key) {
                    if ("topic".equals(key))
                        return TOPIC;

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
                    final Matcher m = p.matcher(serialized);

                    if (m.find() && m.groupCount() == 1)
                        return m.replaceAll(String.format("\"topic\": \"%s\"", TOPIC));

                    final int idx = serialized.lastIndexOf("}");

                    if (idx < 0)
                        return serialized;

                    final String orig = serialized.substring(0, idx);

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
