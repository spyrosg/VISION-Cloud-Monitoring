package gr.ntua.vision.monitoring.metrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * This is used to provide for the cpu load of process. It uses top under the hood.
 */
public class ProccessCPULoad {
    /***/
    private static final String  top = "ps -o pid,comm,pcpu,rss -p";
    /***/
    private final ProcessBuilder builder;


    /**
     * @param pid
     *            the pid of the process to get the cpu load for
     */
    public ProccessCPULoad(final long pid) {
        builder = new ProcessBuilder((top + String.valueOf(pid)).split(" "));
    }


    /**
     * @return the process' cpu load.
     */
    public double get() {
        try {
            return Double.parseDouble(get1());
        } catch (final NumberFormatException e) {
            // NOP
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final InterruptedException e) {
            // NOP
        }

        return 0;
    }


    /**
     * @return the process' cpu load.
     * @throws IOException
     * @throws InterruptedException
     */
    private String get1() throws IOException, InterruptedException {
        builder.redirectErrorStream(true);

        final Process proc = builder.start();

        proc.getOutputStream().close();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line = null, prev = null;

        while ((line = reader.readLine()) != null)
            prev = line;

        reader.close();
        proc.waitFor();

        if (prev == null)
            return "0";

        final String[] fs = prev.split("\\s+");

        return fs[fs.length - 2];
    }
}
