package monitoring.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONStringer;
import org.codehaus.jettison.json.JSONWriter;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import com.sun.jersey.api.representation.Form;


/**
 * Rules test program.
 */
@Path("/push")
public class RulesTest {
    /***/
    static int          actions      = 0;

    /***/
    static final Client client       = Client.create();

    /***/
    static int          measurements = 0;
    static final String Rule0        = "rule \"test rule name\"\n" + //
                                             "when\n" + //
                                             "        Event( Source.Address like \".+\" );\n" + //
                                             "then\n" + //
                                             "         PushAsIs( \"http://10.0.0.34:7070/push\" );\n";


    /**
     * This is the service implementing method.
     * 
     * @param eventJSON
     *            the JSON string which specifies the event.
     * @return the operation status.
     * @throws JSONException
     */
    @POST
    @Produces("application/json")
    public String receiveEvent(@FormParam("event") final String eventJSON) throws JSONException {
        System.out.println( eventJSON );

        // Event event = new EventImpl( new JSONObject( eventJSON ) );
        // switch( event.eventType() )
        // {
        // case Measurement:
        // ++measurements;
        // break;
        // case Action:
        // ++actions;
        // break;
        // }
        //
        // System.out.println( "received event (Type=" + ( event.aggregationCount() == 0 ? "single" : "aggregated" ) + " || A:"
        // + actions + " / M:" + measurements + ")" );
        // System.out.println( "\tDescription: " + event.toJSON() );

        final JSONWriter wr = new JSONStringer().object();
        wr.key( "status" ).value( "ok" );
        return wr.endObject().toString();
    }


    /**
     * @param args
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public static void main(final String[] args) throws IllegalArgumentException, IOException {
        // System.out.println(Rule0);
        // System.out.println();
        // System.out.println();
        // System.out.println();
        // System.out.println();
        // System.out.println(Rule1);

        final Map<String, String> initParams = new HashMap<String, String>();
        initParams.put( "com.sun.jersey.config.property.packages", "gr.ntua.vision.monitoring.test" );

        GrizzlyWebContainerFactory.create( "http://localhost:7070/", initParams );

        System.out.println( "Registering rule." );

        try {
            final Form form = new Form();
            form.add( "rule", Rule0 );
            final String response = client.resource( "http://10.0.2.215:8080/vismo/Monitoring/cloud/registerAggregationRule" )
                    .post( ClientResponse.class, form ).getEntity( String.class );
            System.out.println( response );
        } catch( final Exception e ) {
            e.printStackTrace();
            System.exit( 0 );
        }

        System.out.println( "w8ing 4 events :P" );
    }
}
