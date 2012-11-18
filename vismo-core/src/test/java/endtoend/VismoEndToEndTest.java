package endtoend;

import gr.ntua.vision.monitoring.VMInfo;
import gr.ntua.vision.monitoring.VismoConfiguration;
import gr.ntua.vision.monitoring.VismoFactory;
import gr.ntua.vision.monitoring.VismoService;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;


/**
 * Testing the system end to end: there is an event generator, the main vismo instance and two event consumers.
 */
public class VismoEndToEndTest {
    /***/
    private static final String      clusterHeadIP   = "10.2.2.102";
    /***/
    private final Properties         bprops          = new Properties() {
                                                         /***/
                                                         private static final long serialVersionUID = 1L;

                                                         {
                                                             setProperty("cloud.name", "visioncloud.eu");
                                                             setProperty("cloud.heads", "10.0.2.211, 10.0.2.212");
                                                             setProperty("cluster.name", "vision-test-1");
                                                             setProperty("cluster.head", clusterHeadIP);
                                                             setProperty("producers.point", "tcp://127.0.0.1:46429");
                                                             setProperty("consumers.port", "46430");
                                                             setProperty("udp.port", "46431");
                                                             setProperty("cluster.head.port", "46432");
                                                         }
                                                     };
    /***/
    private VismoService             clusterHead;
    /***/
    private final VMInfo             clusterHeadInfo = new VMInfo() {
                                                         @Override
                                                         public InetAddress getAddress() throws SocketException {
                                                             try {
                                                                 return InetAddress.getByName(clusterHeadIP);
                                                             } catch (final UnknownHostException e) {
                                                                 throw new RuntimeException(e);
                                                             }
                                                         }


                                                         @Override
                                                         public NetworkInterface getInterface() throws SocketException {
                                                             return null;
                                                         }


                                                         @Override
                                                         public int getPID() {
                                                             return 346;
                                                         }
                                                     };
    /***/
    private final VismoConfiguration conf            = new VismoConfiguration(bprops);
    /***/
    private final VismoFactory       factory         = new VismoFactory(conf);
    /***/
    private FakeObjectService        obs;
    /***/
    private VismoService             worker;
    /***/
    private final VMInfo             workerInfo      = new VMInfo() {
                                                         /***/
                                                         private static final String workerIP = "10.2.2.101";


                                                         @Override
                                                         public InetAddress getAddress() throws SocketException {
                                                             try {
                                                                 return InetAddress.getByName(workerIP);
                                                             } catch (final UnknownHostException e) {
                                                                 throw new RuntimeException(e);
                                                             }
                                                         }


                                                         @Override
                                                         public NetworkInterface getInterface() throws SocketException {
                                                             return null;
                                                         }


                                                         @Override
                                                         public int getPID() {
                                                             return 345;
                                                         }
                                                     };


    /**
     * @throws SocketException
     */
    @Before
    public void setUp() throws SocketException {
        worker = factory.build(workerInfo).start();
        clusterHead = factory.build(clusterHeadInfo);
    }


    /**
     * 
     */
    @After
    public void tearDown() {
        worker.stop();
        clusterHead.stop();
    }
}
