package integration.tests;

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.sockets.Socket;
import gr.ntua.vision.monitoring.web.WebAppBuilder;
import gr.ntua.vision.monitoring.web.WebServer;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.RequestBuilder;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * TODO: implement like {@link RulesResourceTest}.
 */
public class HttpEventSourceTest {
	/***/
	private static final int PORT = 9998;
	/***/
	private static final String ROOT_URL = "http://localhost:" + PORT;
	/***/
	private final Client client;
	/***/
	private WebServer server;
	/***/
	private MonitoringEvent e;

	{
		final ClientConfig cc = new DefaultClientConfig();

		cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
		client = Client.create(cc);
	}

	@Test
	public void shouldRejectInvalidEvents() {
		final ClientResponse res = root().path("events").entity("{ \"foo\" : 3 }").put(ClientResponse.class);
		assertEquals ("server should reject invalid events",ClientResponse.Status.BAD_REQUEST,  res.getClientResponseStatus());
	}
	
	
	@Test
	public void shouldAcceptEventsThroughPut() throws UnknownHostException{
		HashMap mapev = new  HashMap();
		long ts = 123456789L;
		mapev.put("timestamp", ts);
		String addr = InetAddress.getLocalHost().getHostAddress();
		mapev.put("originating-service", "service");
		mapev.put("originating-machine", addr);
		mapev.put("topic", "new event");
		JSONObject obj = new JSONObject(mapev);
		final ClientResponse res = root().path("events").accept(MediaType.APPLICATION_JSON).entity(obj.toString()).put(ClientResponse.class);
		assertEquals (ClientResponse.Status.CREATED, res.getClientResponseStatus());
	}

	/**
	 * @return a web resource pointing to the server's root.
	 */
	private WebResource root() {
		return client.resource(ROOT_URL);
	}

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		

		final WebAppBuilder builder = new WebAppBuilder();
		/** the event factory. */
		final EventFactory factory;
		/** the socket to receive messages. */
		final Socket sock;

		server = new WebServer(PORT);
		Application application = builder.addResource(new HttpEventResource())
				.build();

		server.withWebAppAt(application, "/*");
		server.start();
	}
	
	@After
    public void tearDown() throws Exception {
        if (server != null)
            server.stop();
    }
}
