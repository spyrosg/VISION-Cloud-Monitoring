package gr.ntua.vision.monitoring.metrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * This is used to report the used and total memory of the host. It uses the linux <code>free</code> command under the hood.
 */
public class LinuxHostMemoryUsage implements HostMemoryUsage {
    /***/
    private static final String  memoryCommand = "free -b";
    /***/
    private final ProcessBuilder builder;


    /**
     * 
     */
    public LinuxHostMemoryUsage() {
        builder = new ProcessBuilder(memoryCommand.split(" "));
    }


    /**
     * @see gr.ntua.vision.monitoring.metrics.HostMemoryUsage#get()
     */
    @Override
    public HostMemory get() {
        try {
            final String[] arr = get1();

            return new HostMemory(Long.parseLong(arr[0]), Long.parseLong(arr[1]));
        } catch (final IOException e) {
            // NOP
        } catch (final InterruptedException e) {
            // NOP
        }

        return new HostMemory(0, 0);
    }


    /**
     * @return the memory usage of the host.
     * @throws IOException
     * @throws InterruptedException
     */
    private String[] get1() throws IOException, InterruptedException {
        builder.redirectErrorStream(true);

        final Process proc = builder.start();

        proc.getOutputStream().close();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        reader.readLine();

        final String total = reader.readLine().split("\\s+")[1];
        final String used = reader.readLine().split("\\s+")[1];

        reader.close();
        proc.waitFor();

        return new String[] { total, used };
    }
}
