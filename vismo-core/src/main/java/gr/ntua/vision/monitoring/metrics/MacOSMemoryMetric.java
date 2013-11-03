package gr.ntua.vision.monitoring.metrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * This is used to report the used and total memory of the host. It uses the mac os <code>vm_stat</code> command under the hood.
 */
public class MacOSMemoryMetric implements HostMemoryMetric {
    /***/
    private static final int     MAC_OS_PAGE_SIZE = 4096;
    /***/
    private static final String  memoryCommand    = "vm_stat";
    /***/
    private final ProcessBuilder builder          = new ProcessBuilder(memoryCommand);


    /**
     * @see gr.ntua.vision.monitoring.metrics.HostMemoryMetric#get()
     */
    @Override
    public HostMemory get() {
        try {
            final String[] arr = get1();
            final long free = Long.parseLong(arr[0]) * MAC_OS_PAGE_SIZE;
            final long active = Long.parseLong(arr[1]) * MAC_OS_PAGE_SIZE;
            final long inactive = Long.parseLong(arr[2]) * MAC_OS_PAGE_SIZE;
            final long wired = Long.parseLong(arr[3]) * MAC_OS_PAGE_SIZE;

            return new HostMemory(free + active + inactive + wired, active + inactive + wired);
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

        final String free = reader.readLine().split("\\s+")[2].replace(".", "");
        final String active = reader.readLine().split("\\s+")[2].replace(".", "");
        final String inactive = reader.readLine().split("\\s+")[2].replace(".", "");
        reader.readLine();
        final String wired = reader.readLine().split("\\s+")[3].replace(".", "");

        reader.close();
        proc.waitFor();

        return new String[] { free, active, inactive, wired };
    }
}
