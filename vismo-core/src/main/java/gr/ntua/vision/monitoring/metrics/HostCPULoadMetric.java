package gr.ntua.vision.monitoring.metrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Provides for the host cpu load. Uses the linux <code>uptime</code> command. The value reported is the system load average for
 * the last minute.
 */
public class HostCPULoadMetric {
    /***/
    private static final String  uptime  = "uptime";
    /***/
    private final ProcessBuilder builder = new ProcessBuilder(uptime);


    /**
     * 
     */
    public HostCPULoadMetric() {
    }


    /**
     * @return the host's cpu load.
     */
    public double get() {
        try {
            return Double.parseDouble(get1().replace(",", ""));
        } catch (final NumberFormatException e) {
            // NOP
        } catch (final IOException e) {
            // NOP
        } catch (final InterruptedException e) {
            // NOP
        }

        return 0;
    }


    /**
     * @return the host's cpu load.
     * @throws IOException
     * @throws InterruptedException
     */
    private String get1() throws IOException, InterruptedException {
        final Process proc = builder.start();

        proc.getOutputStream().close();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        final String line = reader.readLine();
        final String[] fs = line.split(" ");

        reader.close();
        proc.waitFor();

        // NOTE: the last three fields are the load averages for the last 1, 5, 15 minutes
        return fs[fs.length - 3];
    }
}
