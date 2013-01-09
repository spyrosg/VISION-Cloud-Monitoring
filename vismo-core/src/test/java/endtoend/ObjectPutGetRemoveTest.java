package endtoend;

import gr.ntua.vision.monitoring.notify.VismoEventRegistry;
import gr.ntua.vision.monitoring.zmq.ZMQFactory;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;

import com.eclipsesource.restfuse.Assert;
import com.eclipsesource.restfuse.Destination;
import com.eclipsesource.restfuse.HttpJUnitRunner;
import com.eclipsesource.restfuse.Method;
import com.eclipsesource.restfuse.Response;
import com.eclipsesource.restfuse.annotation.Context;
import com.eclipsesource.restfuse.annotation.HttpTest;


/**
 * @author tmessini
 */
@RunWith(HttpJUnitRunner.class)
public class ObjectPutGetRemoveTest {
    /***/
    private static final String     CLOUDHEAD_ADDRESS = "10.0.1.103";
    /***/
    private static final Logger     log               = LoggerFactory.getLogger(ObjectPutGetRemoveTest.class);
    /***/
    private static final String     OBJECT_PATH       = "/vision-cloud/object-service/ntua/endtoendtest/object1";
    /***/
    private static final ZMQFactory socketFactory     = new ZMQFactory(new ZContext());
    /***/
    @Rule
    public Destination              restfuse          = new Destination("http://" + ObjectPutGetRemoveTest.CLOUDHEAD_ADDRESS);
    /***/
    final VismoEventRegistry        registry          = new VismoEventRegistry(socketFactory, "tcp://10.0.1.103:56430");
    /***/
    @Context
    private Response                response;


    /**
     * checks the deletion of an object in the object service
     */
    @Ignore
    @HttpTest(method = Method.DELETE, path = ObjectPutGetRemoveTest.OBJECT_PATH, content = "thodoris test object")
    public void checkDeleteObject() {
        ObjectPutGetRemoveTest.log.debug("delete object reply code: {}", response.getStatus() + ".");
        Assert.assertNoContent(response);
    }


    /**
     * checks the get of an object from the object service
     */
    @Ignore
    @HttpTest(method = Method.GET, path = ObjectPutGetRemoveTest.OBJECT_PATH, content = "thodoris test object")
    public void checkGetObject() {
        ObjectPutGetRemoveTest.log.debug("get object reply code: {}", response.getStatus() + ".");
        Assert.assertOk(response);
    }


    /**
     * checks the put of an object in the object service
     */
    @Ignore
    @HttpTest(method = Method.PUT, path = ObjectPutGetRemoveTest.OBJECT_PATH, content = "thodoris test object")
    public void checkPutObject() {
        ObjectPutGetRemoveTest.log.debug("put object reply code: {}", response.getStatus() + ".");
        Assert.assertCreated(response);
    }


    /**
     * registers a fake event consumer
     */
    @Before
    public void createConsumer() {
        registry.registerToAll(new LoggingHandler());
    }

}
