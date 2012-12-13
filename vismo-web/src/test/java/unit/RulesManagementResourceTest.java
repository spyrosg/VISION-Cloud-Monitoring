package unit;

import static com.eclipsesource.restfuse.Assert.assertOk;
import static com.eclipsesource.restfuse.Assert.assertNoContent;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;

import com.eclipsesource.restfuse.Destination;
import com.eclipsesource.restfuse.HttpJUnitRunner;
import com.eclipsesource.restfuse.Method;
import com.eclipsesource.restfuse.Response;
import com.eclipsesource.restfuse.annotation.Context;
import com.eclipsesource.restfuse.annotation.HttpTest;

import gr.ntua.vision.monitoring.web.RulesWebServer;


@RunWith(HttpJUnitRunner.class)
public class RulesManagementResourceTest {
    static RulesWebServer server = new RulesWebServer();


    @BeforeClass
    public static void startServer() throws IllegalArgumentException, IOException {
        server.start();
    }

    @Rule
    public Destination restfuse = new Destination("http://localhost:9998");

    @Context
    private Response   response;


    /**
     * checks the insertion of a rule
     */
    @HttpTest(method = Method.PUT, path = "rules/Aggregation-default/1/The-default1-aggregation")
    public void checkRestPutRule1() {
        assertOk(response);
    }


    /**
     * checks the retrieval of a rule
     */
    @HttpTest(method = Method.GET, path = "rules/1")
    public void checkRestGetRule1() {
        assertOk(response);
    }


    /**
     * checks the deletion of a rule
     */
    @HttpTest(method = Method.DELETE, path = "rules/1")
    public void checkRestDeleteRule1() {
        assertOk(response);
    }


    /**
     * checks the retrieval of a rule after deletion
     */
    @HttpTest(method = Method.GET, path = "rules/1")
    public void checkRestGetRule1AfterDelete() {
        assertNoContent(response);
    }


    @AfterClass
    public static void stopServer() throws IllegalArgumentException, IOException {
        server.stop();
    }

}
