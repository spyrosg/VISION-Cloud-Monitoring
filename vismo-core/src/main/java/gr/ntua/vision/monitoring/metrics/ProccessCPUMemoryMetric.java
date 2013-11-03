package gr.ntua.vision.monitoring.metrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * This is used to provide for the cpu load of a process, averaged over all processors, and the total memory used by that process.
 * It uses the linux <code>ps</code> command under the hood.
 */
public class ProccessCPUMemoryMetric {
    /**
     *
     */
    public static class CPUMemory {
        /***/
        public final double cpuLoad;
        /***/
        public final int    memoryUsage;


        /**
         * @param cpuLoad
         * @param memoryUsage
         */
        public CPUMemory(final double cpuLoad, final int memoryUsage) {
            this.cpuLoad = cpuLoad;
            this.memoryUsage = memoryUsage;
        }
    }

    /***/
    private static final int     noProcessors = Runtime.getRuntime().availableProcessors();
    /***/
    private static final String  psCommand    = "ps -o pid,comm,pcpu,rss -p";
    /***/
    private final ProcessBuilder builder;


    /**
     * @param pid
     *            the pid of the process to get the cpu load for
     */
    public ProccessCPUMemoryMetric(final long pid) {
        builder = new ProcessBuilder((psCommand + String.valueOf(pid)).split(" "));
    }


    /**
     * @return the process' cpu load.
     */
    public CPUMemory get() {
        try {
            final String[] arr = get1();

            return new CPUMemory(Double.parseDouble(arr[0]) / (100 * noProcessors), Integer.parseInt(arr[1]));
        } catch (final NumberFormatException e) {
            // NOP
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final InterruptedException e) {
            // NOP
        }

        return new CPUMemory(0, 0);
    }


    /**
     * @return the process' cpu load.
     * @throws IOException
     * @throws InterruptedException
     */
    private String[] get1() throws IOException, InterruptedException {
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
            return new String[] { "0", "0" };

        final String[] fs = prev.split("\\s+");

        return new String[] { fs[fs.length - 2], fs[fs.length - 1] };
    }
}
