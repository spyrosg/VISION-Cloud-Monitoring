package gr.ntua.vision.monitoring.mon;

import gr.ntua.vision.monitoring.VismoConfiguration;

import java.net.UnknownHostException;


/**
 * This is used to provide {@link VismoGroupService}s.
 */
public class VismoGroupServiceFactory {
    /** the configuration object. */
    private final VismoConfiguration conf;


    /**
     * Constructor.
     * 
     * @param conf
     *            the configuration object.
     */
    public VismoGroupServiceFactory(final VismoConfiguration conf) {
        this.conf = conf;
    }


    /**
     * Construct and setup a {@link VismoGroupService} object.
     * 
     * @return a {@link VismoGroupService} object.
     * @throws UnknownHostException
     */
    public VismoGroupService build() throws UnknownHostException {
        final VismoGroupServer server = new VismoGroupServer(conf);
        final Thread t = new Thread(server, "group-server");
        final GroupMembership mship = new GroupMembership();

        server.register(new AddGroupMember(new GroupElementFactory(), mship));
        t.setDaemon(true);

        return new VismoGroupService(t, new PrintGroupTask(mship));
    }
}
