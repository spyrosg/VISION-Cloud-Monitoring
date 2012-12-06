package endtoend;

import static com.eclipsesource.restfuse.Assert.assertOk;
import static com.eclipsesource.restfuse.Assert.assertNoContent;
import static com.eclipsesource.restfuse.Assert.assertCreated;

import gr.ntua.vision.monitoring.notify.EventRegistry;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;

import org.junit.runner.RunWith;

import com.eclipsesource.restfuse.Destination;
import com.eclipsesource.restfuse.HttpJUnitRunner;
import com.eclipsesource.restfuse.Method;
import com.eclipsesource.restfuse.Response;
import com.eclipsesource.restfuse.annotation.Context;
import com.eclipsesource.restfuse.annotation.HttpTest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(HttpJUnitRunner.class)
public class ObjectPutGetRemoveTest {

    private static final String CLOUDHEAD_ADDRESS = "10.0.1.103";

    private static final String OBJECT_PATH       = "/vision-cloud/object-service/ntua/endtoendtest/object1";

    private static final Logger log               = LoggerFactory.getLogger(ObjectPutGetRemoveTest.class);
    
    final EventRegistry registry = new EventRegistry("tcp://10.0.1.103:56430");
    
    @Rule
    public Destination          restfuse          = new Destination("http://" + CLOUDHEAD_ADDRESS);

    @Context
    private Response            response;


    @Before
    public void createConsumer()
    {
        registry.registerToAll(new FakeEventConsumer.LoggingHandler());
    }
    
    
    /**
     * checks the put of an object in the object service
     */
    @Ignore
    @HttpTest(method = Method.PUT, path = OBJECT_PATH, content = "thodoris test object")
    public void checkPutObject() {
        log.debug("put object reply code: {}", response.getStatus() + ".");
        assertCreated(response);
    }


    /**
     * checks the get of an object from the object service
     */
    @Ignore
    @HttpTest(method = Method.GET, path = OBJECT_PATH, content = "thodoris test object")
    public void checkGetObject() {
        log.debug("get object reply code: {}", response.getStatus() + ".");
        assertOk(response);
    }


    /**
     * checks the deletion of an object in the object service
     */
    @Ignore
    @HttpTest(method = Method.DELETE, path = OBJECT_PATH, content = "thodoris test object")
    public void checkDeleteObject() {
        log.debug("delete object reply code: {}", response.getStatus() + ".");
        assertNoContent(response);
    }

}
