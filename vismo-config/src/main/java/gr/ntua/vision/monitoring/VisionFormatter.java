package gr.ntua.vision.monitoring;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;


/**
 * A custom log formatter. The format should match the following logback notation:
 * <code>%-5p [%d{ISO8601," + timeZone.getID() + "}] %c: %m\n%ex</code>.
 */
public class VisionFormatter extends Formatter {
    // INFO [2012-06-11 10:05:42,525] gr.ntua.vision.monitoring.MonitoringInstance: Starting up, pid=28206, ip=vis0/10.0.0.10
    /***/
    private final DateFormat fmt;


    /**
     * Constructor.
     */
    public VisionFormatter() {
        this.fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        this.fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
    }


    /**
     * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
     */
    @Override
    public String format(final LogRecord r) {
        final String s = String.format("%-6s [%s] %s: %s\n", r.getLevel(), fmt.format(new Date(r.getMillis())),
                                       r.getSourceClassName(), r.getMessage());

        if (r.getThrown() != null) {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);

            r.getThrown().printStackTrace(pw);

            return s + sw.toString();
        }

        return s;
    }
}
