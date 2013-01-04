package endtoend;

import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;

import com.eclipsesource.restfuse.Assert;
import com.eclipsesource.restfuse.AuthenticationType;
import com.eclipsesource.restfuse.Destination;
import com.eclipsesource.restfuse.HttpJUnitRunner;
import com.eclipsesource.restfuse.Method;
import com.eclipsesource.restfuse.Response;
import com.eclipsesource.restfuse.annotation.Authentication;
import com.eclipsesource.restfuse.annotation.Context;
import com.eclipsesource.restfuse.annotation.HttpTest;


/**
 * @author tmessini
 */
@RunWith(HttpJUnitRunner.class)
public class ContainerPutRemoveTest {
    /***/
    private static final String     CLOUDHEAD_ADDRESS = "10.0.1.103:8080";

    /***/
    private static final String     CONTAINER_PATH    = "/containers/ntua/testcontainer";

    /***/
    private static final Logger     log               = LoggerFactory.getLogger(ContainerPutRemoveTest.class);

    /***/
    private static final ZMQSockets zmq               = new ZMQSockets(new ZContext());

    /***/
    @Rule
    public Destination              restfuse          = new Destination("http://" + ContainerPutRemoveTest.CLOUDHEAD_ADDRESS);

    /***/
    final VismoEventRegistry        registry          = new VismoEventRegistry(zmq, "tcp://10.0.1.103:56430");

    /***/
    @Context
    private Response                response;


    /***/
    @Before
    public void createConsumer() {
        registry.registerToAll(new FakeEventConsumer.LoggingHandler());
    }


    /**
     * checks the creation of the test container
     */
    @Ignore
    @HttpTest(method = Method.PUT, path = ContainerPutRemoveTest.CONTAINER_PATH, authentications = { @Authentication(user = "vasillis@ntua", password = "123", type = AuthenticationType.BASIC) })
    public void createContainerForTest() {
        ContainerPutRemoveTest.log.debug("put container reply code: {}", response.getBody(String.class) + ".");
        Assert.assertCreated(response);
    }


    /**
     * checks the deletion of the test container
     */
    @Ignore
    @HttpTest(method = Method.DELETE, path = ContainerPutRemoveTest.CONTAINER_PATH, authentications = { @Authentication(user = "vasillis@ntua", password = "123", type = AuthenticationType.BASIC) })
    public void deleteContainerForTest() {
        ContainerPutRemoveTest.log.debug("delete container reply code: {}", response.getBody(String.class) + ".");
        Assert.assertOk(response);
    }

}
