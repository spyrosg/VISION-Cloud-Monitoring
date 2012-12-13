package unit;

import gr.ntua.vision.monitoring.web.RulesWebServer;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;

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
public class RulesManagementResourceTest {
    /***/
    private static RulesWebServer server   = new RulesWebServer();

    /***/
    @Rule
    public Destination            restfuse = new Destination("http://localhost:9998");

    /***/
    @Context
    private Response              response;


    /**
     * @return server
     */
    public static RulesWebServer getServer() {
        return RulesManagementResourceTest.server;
    }


    /**
     * @param server
     */
    public static void setServer(final RulesWebServer server) {
        RulesManagementResourceTest.server = server;
    }


    /**
     * @throws IllegalArgumentException
     * @throws IOException
     */
    @BeforeClass
    public static void startServer() throws IllegalArgumentException, IOException {
        RulesManagementResourceTest.getServer().start();
    }


    /**
     * stops the server
     * 
     * @throws IllegalArgumentException
     * @throws IOException
     */
    @AfterClass
    public static void stopServer() throws IllegalArgumentException, IOException {
        RulesManagementResourceTest.getServer().stop();
    }


    /**
     * checks the deletion of a rule
     */
    @HttpTest(method = Method.DELETE, path = "rules/1")
    public void checkRestDeleteRule1() {
        Assert.assertOk(response);
    }


    /**
     * checks the retrieval of a rule
     */
    @HttpTest(method = Method.GET, path = "rules/1")
    public void checkRestGetRule1() {
        Assert.assertOk(response);
    }


    /**
     * checks the retrieval of a rule after deletion
     */
    @HttpTest(method = Method.GET, path = "rules/1")
    public void checkRestGetRule1AfterDelete() {
        Assert.assertNoContent(response);
    }


    /**
     * checks the insertion of a rule
     */
    @HttpTest(method = Method.PUT, path = "rules/Aggregation-default/1/The-default1-aggregation")
    public void checkRestPutRule1() {
        Assert.assertOk(response);
    }

}
