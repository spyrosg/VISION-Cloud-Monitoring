package gr.ntua.vision.monitoring.mon;

import gr.ntua.vision.monitoring.VismoConfiguration;

import java.io.IOException;


/**
 * 
 */
public class Main {
    /**
     * @param args
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {
        if (args.length < 2) {
            System.err.println("arg count");
            System.err.println("usage: config.properties print-period");
            System.exit(1);
        }

        final VismoConfiguration conf = new VismoConfiguration(args[0]);
        final VismoGroupMonitoring mon = new VismoGroupMonitoring(conf);

        mon.start(1000 * Long.parseLong(args[1]));
    }
}
