package endtoend.tests;

import static org.junit.Assert.assertEquals;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.mon.AddGroupMember;
import gr.ntua.vision.monitoring.mon.GroupElement;
import gr.ntua.vision.monitoring.mon.GroupMembership;
import gr.ntua.vision.monitoring.mon.GroupProc;
import gr.ntua.vision.monitoring.mon.VismoGroupClient;
import gr.ntua.vision.monitoring.mon.VismoGroupMonitoring;
import gr.ntua.vision.monitoring.mon.VismoGroupServer;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;


/**
 * 
 */
public class VismoGroupMonitoringTest {
    /***/
    private static class VismoGroupElement {
        /***/
        public final VismoGroupClient client;
        /***/
        public final String           id;


        /**
         * Constructor.
         * 
         * @param id
         * @param client
         */
        public VismoGroupElement(final String id, final VismoGroupClient client) {
            this.id = id;
            this.client = client;
        }


        /**
         * @throws IOException
         */
        public void sendPing() throws IOException {
            client.notifyGroup(id);
        }
    }

    /***/
    private static final long         PERIOD  = 1000;
    /***/
    final ArrayList<GroupElement>     members = new ArrayList<GroupElement>();
    /***/
    private VismoConfiguration        conf;
    /***/
    private final VismoGroupElement[] group   = new VismoGroupElement[3];
    /***/
    private VismoGroupMonitoring      mon;
    /***/
    private final GroupMembership     mship   = new GroupMembership(PERIOD);
    /***/
    @SuppressWarnings("serial")
    private final Properties          props   = new Properties() {
                                                  {
                                                      setProperty("mon.group.addr", "228.5.6.8");
                                                      setProperty("mon.group.port", "12345");
                                                      setProperty("mon.ping.period", String.valueOf(PERIOD));
                                                  }
                                              };


    /**
     * @throws UnknownHostException
     */
    @Before
    public void setUp() throws UnknownHostException {
        conf = new VismoConfiguration(props);
        mon = new VismoGroupMonitoring(new VismoGroupServer(conf));

        mon.register(new AddGroupMember(mship));

        for (int i = 0; i < group.length; ++i)
            group[i] = new VismoGroupElement("foo" + i, new VismoGroupClient(conf));
    }


    /**
     * @throws InterruptedException
     * @throws IOException
     */
    @Test
    public void shouldMonitoringIncomingAndLostClients() throws InterruptedException, IOException {
        mon.start();
        Thread.sleep(100); // spin thread

        clientsSendPings();

        Thread.sleep(7 * conf.getMonPingPeriod() / 8); // make sure we don't expire
        assertLiveMembers();
    }


    /**
     * 
     */
    private void assertLiveMembers() {
        collectMembers();
        System.err.println(members);
        assertEquals(3, members.size());
    }


    /**
     * @throws IOException
     */
    private void clientsSendPings() throws IOException {
        for (int i = 0; i < group.length; ++i)
            group[i].sendPing();
    }


    /**
     * 
     */
    private void collectMembers() {
        members.clear();
        mship.forEach(new GroupProc() {
            @Override
            public void applyTo(final GroupElement member) {
                members.add(member);
            }
        });
    }
}
