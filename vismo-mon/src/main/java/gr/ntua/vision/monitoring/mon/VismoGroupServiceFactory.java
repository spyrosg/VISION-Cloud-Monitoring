package gr.ntua.vision.monitoring.mon;

import gr.ntua.vision.monitoring.VismoConfiguration;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;


/**
 * This is used to provide {@link VismoGroupService}s.
 */
public class VismoGroupServiceFactory {
    /** the configuration object. */
    private final VismoConfiguration conf;
    /** expiration period in milliseconds */
    private final long               expirationPeriod = TimeUnit.SECONDS.convert(90, TimeUnit.SECONDS);


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
        final GroupMembership mship = new GroupMembership(expirationPeriod);

        server.register(new AddGroupMember(new GroupElementFactory(), mship));

        return new VismoGroupService(t, new PrintGroupTask(mship));
    }
}
