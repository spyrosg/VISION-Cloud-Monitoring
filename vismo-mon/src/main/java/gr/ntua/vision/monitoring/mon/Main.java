package gr.ntua.vision.monitoring.mon;

import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.mon.resources.GroupMembershipResource;
import gr.ntua.vision.monitoring.web.WebAppBuilder;
import gr.ntua.vision.monitoring.web.WebServer;


/**
 * 
 */
public class Main {
    /***/
    private static final int PORT = 9002;


    /**
     * @param args
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        if (args.length < 1) {
            System.err.println("arg count");
            System.err.println("usage: config.properties [print-period]");
            System.exit(1);
        }

        final VismoConfiguration conf = new VismoConfiguration(args[0]);
        final long expungePeriod = 2 * conf.getMonPingPeriod() + 1;
        final GroupMembership mship = new GroupMembership(expungePeriod);
        final VismoGroupServer groupServer = new VismoGroupServer(conf);
        final VismoGroupMonitoring mon = new VismoGroupMonitoring(groupServer);
        final WebServer server = new WebServer(PORT);

        mon.register(new AddGroupMember(mship));

        if (args.length == 2)
            mon.addTask(1000 * Long.parseLong(args[1]), new PrintGroupTask(mship));

        mon.start();

        server.withWebAppAt(WebAppBuilder.buildFrom(new GroupMembershipResource(mship)), "/api*")
                .withStaticResourcesAt("/static", "/*");

        server.start();
    }
}
