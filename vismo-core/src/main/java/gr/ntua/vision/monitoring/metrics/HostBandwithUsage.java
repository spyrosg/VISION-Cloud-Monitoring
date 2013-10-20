package gr.ntua.vision.monitoring.metrics;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;


/**
 *
 */
public class HostBandwithUsage {
    /**
     *
     */
    public static class Bandwidth {
        /***/
        public final long inbound;
        /***/
        public final long outbound;


        /**
         * @param inbound
         * @param outbound
         */
        public Bandwidth(final long inbound, final long outbound) {
            this.inbound = inbound;
            this.outbound = outbound;
        }
    }
    /***/
    private static final String  NET_FILE = "/proc/net/dev";

    /***/
    private final BufferedReader reader;


    /**
     * @throws FileNotFoundException
     */
    public HostBandwithUsage() throws FileNotFoundException {
        this.reader = new BufferedReader(new FileReader(NET_FILE));
    }


    /**
     * @return
     */
    public Bandwidth get() {
        try {
            final long[] arr = get1();

            return new Bandwidth(arr[0], arr[1]);
        } catch (final IOException e) {
            // NOP
        }

        return new Bandwidth(0, 0);
    }


    /**
     * @return
     * @throws IOException
     */
    private long[] get1() throws IOException {
        reader.readLine();
        reader.readLine();

        long totalInbound = 0;
        long totalOutbound = 0;
        String line = null;

        while ((line = reader.readLine()) != null) {
            final String[] fs = line.split("\\s+");

            System.err.println(Arrays.toString(fs));
            totalInbound += Long.parseLong(fs[0].split(":")[1]);
            totalOutbound += Long.parseLong(fs[8]);
        }

        return new long[] { totalInbound, totalOutbound };
    }
}
