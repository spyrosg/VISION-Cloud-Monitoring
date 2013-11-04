package gr.ntua.vision.monitoring.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to report the total inbound/outbound bandwith usage of the host. It is using the linux <code>/proc/net/dev</code>
 * under the hood.
 */
public class HostBandwithMetric {
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
    private static final Logger    log      = LoggerFactory.getLogger(HostBandwithMetric.class);
    /***/
    private static final String    NET_FILE = "/proc/net/dev";
    /***/
    private final RandomAccessFile file;


    /**
     * @throws FileNotFoundException
     */
    public HostBandwithMetric() throws FileNotFoundException {
        this.file = new RandomAccessFile(NET_FILE, "r");
    }


    /**
     * @return the inbound/outbound in bytes.
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
     * @return the inbound/outbound in bytes.
     * @throws IOException
     */
    private long[] get1() throws IOException {
        file.readLine();
        file.readLine();

        long totalInbound = 0;
        long totalOutbound = 0;
        String line = null;

        while ((line = file.readLine()) != null) {
            final int idx = line.indexOf(":");

            if (idx < 0)
                continue;

            final String[] fs = line.substring(idx + 1).trim().split("\\s+");

            log.debug("fields: {}", java.util.Arrays.toString(fs));
            totalInbound += Long.parseLong(fs[0]);
            totalOutbound += Long.parseLong(fs[8]);
        }

        file.seek(0);

        return new long[] { totalInbound, totalOutbound };
    }
}
