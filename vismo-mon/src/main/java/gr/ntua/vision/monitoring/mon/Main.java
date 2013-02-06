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
        final GroupMembership mship = new GroupMembership(2 * conf.getMonPingPeriod() + 1);
        final VismoGroupServer server = new VismoGroupServer(conf);
        final VismoGroupMonitoring mon = new VismoGroupMonitoring(server);

        mon.register(new AddGroupMember(mship));
        mon.addTask(1000 * Long.parseLong(args[1]), new PrintGroupTask(mship));
        mon.start();
    }
}
