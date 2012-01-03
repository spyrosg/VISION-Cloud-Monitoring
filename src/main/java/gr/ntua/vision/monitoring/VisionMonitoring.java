package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.model.Event.EventType;
import gr.ntua.vision.monitoring.model.Location;
import gr.ntua.vision.monitoring.model.Resource;
import gr.ntua.vision.monitoring.model.impl.EventImpl;
import gr.ntua.vision.monitoring.model.impl.LocationImpl;
import gr.ntua.vision.monitoring.model.impl.ResourceImpl;
import it.eng.compliance.xdas.parser.XDasEventType;
import it.eng.compliance.xdas.parser.XdasOutcomes;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jms.JMSException;

import org.codehaus.jettison.json.JSONException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.representation.Form;


/**
 * The monitoring library singleton.
 * <p>
 * The singleton should be initialized prior to its use. Each component should choose a UUID and call
 * {@link VisionMonitoring#initialize(String, UUID)} with it. The first argument should be the Monitoring Cluster instance event
 * receiving interface. Assuming the cluster instance runs on the IP <code>X.Y.Z.W</code>, then the address is
 * <code>http://X.Y.Z.W:8080/vismo/Monitoring/push/event</code>. Thus, the singleton can be initialized with the following:
 * <blockquote><code><br>UUID appId = UUID.randomUUID();<br>
 * VisionMonitoring.initialize( "http://10.0.2.111:8080/vismo/Monitoring/push/event", appId ); </code></blockquote> As soon as the
 * singleton is initialized, then its {@link VisionMonitoring#log(URL, URL, String, String, int, int, Map, List)} method may be
 * called to commit an event. For example:<blockquote><code> <br>
 * List&lt;Resource&gt; resources = Lists.newArrayList();<br>
 * resources.add( new ResourceImpl( "memory", "MB", Math.random() * 3096 ) );<br>
 * resources.add( new ResourceImpl( "storage", "GB", Math.random() * 1024 ) );<br>
 * <br>
 * String[] action = Actions[Math.min( (int) Math.floor( Actions.length * Math.random() ), Actions.length - 1 )];<br>
 * <br>
 * URL src = new URL( "http://localhost:7070/some/component/" );<br>
 * URL trg = new URL( "http://otherhost:9090/some/other/component/" );<br>
 * <br>
 * Map<String, String> params = new HashMap<String, String>();<br>
 * params.put( "foo", "bar" );<br>
 * params.put( "foo1", "bar1" );<br>
 * params.put( "foo2", "bar2" );<br>
 * <br>
 * VisionMonitoring.instance().log( src, trg, "foo", "bar",<br>
 * XDasEventType.XDAS_AE_MODIFY_DATA_ITEM_ASSOC_CONTEXT.getEventCode(),<br>
 * XdasOutcomes.XDAS_OUT_SUCCESS.getOutcomeCode(), params, resources );<br>
 * </code></blockquote>
 * </p>
 */
public class VisionMonitoring {
    /** empty parameters */
    public static final Map<String, String> EmptyParameters = Collections.unmodifiableMap( Maps.<String, String> newHashMap() );
    /** empty resource list. */
    public static final List<Resource>      EmptyResources  = Collections.unmodifiableList( Lists.<Resource> newArrayList() );
    /** the client used to make HTTP requests. */
    private static final Client             client          = Client.create();
    /** the single object's instance. */
    private static VisionMonitoring         instance;

    /** the id of this component. */
    private final UUID                      id;
    /** the monitoring URL */
    private final String                    url;

    static {
        client.setConnectTimeout( 1000 );
    }


    /**
     * c/tor.
     * 
     * @param url
     * @param id
     */
    private VisionMonitoring(final String url, final UUID id) {
        this.url = url;
        this.id = id;
    }


    /**
     * log an action
     * 
     * @param source
     *            action source
     * @param target
     *            action target. It may be <code>null</code>.
     * @param user
     *            user performing the action, if any, otherwise <code>null</code>.
     * @param tenant
     *            tenant performing the action, if any, otherwise <code>null</code>.
     * @param xdasType
     *            type of action. Use the {@link XDasEventType}.*.{@link XDasEventType#getEventCode()} to fill this.
     * @param xdasStatus
     *            status of action. Use the {@link XdasOutcomes}.*.{@link XdasOutcomes#getOutcomeCode()} to fill this.
     * @param parameters
     *            parameters of action (action specific data). This can be filled in with any values necessary to describe the
     *            action at the detail level required.
     * @param resources
     *            the list with all resources that are consumed by the logged action. The default resource implementation is
     *            {@link ResourceImpl}.
     * @throws Exception
     */
    public void log(final URL source, final URL target, final String user, final String tenant, final int xdasType,
            final int xdasStatus, final Map<String, String> parameters, final List<Resource> resources) throws Exception {
        final StringBuilder buf = new StringBuilder();
        for( final Map.Entry<String, String> prm : parameters.entrySet() ) {
            if( buf.length() > 0 )
                buf.append( ',' );
            buf.append( prm.getKey() );
            buf.append( '=' );
            buf.append( prm.getValue() );
        }
        final String paramsBuf = buf.toString();

        // pushXdas( source, target, user, tenant, xdasType, xdasStatus, paramsBuf );
        pushEvent( source, target, user, tenant, resources, paramsBuf );
    }


    /**
     * push an event to the monitoring system.
     * 
     * @param event
     *            the event to push.
     */
    private void push(final Event event) {
        try {
            final Form form = new Form();
            form.add( "event", event.toJSON().toString() );

            client.resource( url ).post( form );
        } catch( final JSONException x ) {
            x.printStackTrace();
        }
    }


    /**
     * generate and push the event that is emitted by the given data.
     * 
     * @param source
     *            action source
     * @param target
     *            action target. It may be <code>null</code>.
     * @param user
     *            user performing the action
     * @param tenant
     *            tenant performing the action
     * @param resources
     *            the list with all resources that are consumed by the logged action.
     * @param paramsBuf
     *            a CSV string with the action parameters.
     * @throws UnknownHostException
     */
    private void pushEvent(final URL source, final URL target, final String user, final String tenant,
            final List<Resource> resources, final String paramsBuf) throws UnknownHostException {
        final long now = new Date().getTime();
        final Location src = new LocationImpl( source.getHost(), source.getPath(), user, tenant, InetAddress
                .getByName( source.getHost() ).getHostAddress() );
        Location trg = null;
        if( target != null ) //
            trg = new LocationImpl( target.getHost(), target.getPath(), user, tenant, InetAddress.getByName( target.getHost() )
                    .getHostAddress() );

        final String description = String.format( "%s#%s/%s[%s]@%s", source, user, tenant, paramsBuf.toString(), target );

        final Event event = new EventImpl( null, id, description, EventType.Action, resources, now, now, src, trg, null );

        push( event );
    }


    /**
     * initialize the single instance.
     * 
     * @param url
     *            the cluster monitoring URL.
     * @param id
     *            the hot component's ID.
     * @return the instance created.
     * @throws JMSException
     */
    public static VisionMonitoring initialize(final String url, final UUID id) throws JMSException {
        return instance = new VisionMonitoring( url, id );
    }


    /**
     * get the single monitoring instance.
     * 
     * @return the instance.
     */
    public static VisionMonitoring instance() {
        return instance;
    }
}
