package endtoend;

import static com.eclipsesource.restfuse.Assert.assertOk;
import static com.eclipsesource.restfuse.Assert.assertCreated;
import gr.ntua.vision.monitoring.notify.EventRegistry;

import org.junit.Rule;
import org.junit.Before;

import org.junit.runner.RunWith;

import com.eclipsesource.restfuse.AuthenticationType;
import com.eclipsesource.restfuse.Destination;
import com.eclipsesource.restfuse.HttpJUnitRunner;
import com.eclipsesource.restfuse.Method;
import com.eclipsesource.restfuse.Response;
import com.eclipsesource.restfuse.annotation.Authentication;
import com.eclipsesource.restfuse.annotation.Context;
import com.eclipsesource.restfuse.annotation.HttpTest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author tmessini
 *
 */
@RunWith(HttpJUnitRunner.class)
public class ContainerPutRemoveTest {
    
    /**
     * 
     */
    private static final String CLOUDHEAD_ADDRESS = "10.0.1.103:8080";

    /**
     * 
     */
    private static final String CONTAINER_PATH    = "/containers/ntua/testcontainer";

    /**
     * 
     */
    private static final Logger log               = LoggerFactory.getLogger(ContainerPutRemoveTest.class);
    
    /**
     * 
     */
    final EventRegistry registry = new EventRegistry("tcp://10.0.1.103:56430");

    /**
     * 
     */
    @Rule
    public Destination          restfuse          = new Destination("http://" + CLOUDHEAD_ADDRESS);

    /**
     * 
     */
    @Context
    private Response            response;


    /**
     * 
     */
    @Before
    public void createConsumer()
    {
        registry.registerToAll(new FakeEventConsumer.LoggingHandler());
    }
    
    /**
     * checks the creation of the test container
     */
    @HttpTest(method = Method.PUT, path = CONTAINER_PATH, authentications = { @Authentication(user = "vasillis@ntua", password = "123", type = AuthenticationType.BASIC) })
    public void createContainerForTest() {
        log.debug("put container reply code: {}", response.getBody(String.class) + ".");
        assertCreated(response);
    }


    /**
     * checks the deletion of the test container
     */
    @HttpTest(method = Method.DELETE, path = CONTAINER_PATH, authentications = { @Authentication(user = "vasillis@ntua", password = "123", type = AuthenticationType.BASIC) })
    public void deleteContainerForTest() {
        log.debug("delete container reply code: {}", response.getBody(String.class) + ".");
        assertOk(response);
    }
    


}
