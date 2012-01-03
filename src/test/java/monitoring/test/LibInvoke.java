package monitoring.test;

import gr.ntua.vision.monitoring.VisionMonitoring;
import gr.ntua.vision.monitoring.model.Resource;
import gr.ntua.vision.monitoring.model.impl.ResourceImpl;
import it.eng.compliance.xdas.parser.XDasEventType;
import it.eng.compliance.xdas.parser.XdasOutcomes;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/**
 * Library invoker.
 */
public class LibInvoke {
    /***/
    static final String[][]     Actions       = {
            { "http://10.0.2.111:8080/vismo/LibInvoke", "http://10.0.2.111:8080/vismo/Monitoring", "type=event" }, //
            { "http://10.0.2.111:8080/vismo/Monitoring/cluster", "http://10.0.2.111:8080/vismo/Monitoring/cloud", "type=event" }, //
            { "http://10.0.2.111:8080/vismo/LibInvoke", null }, //
            { "http://147.102.19.45/myvisionhost/ManagerContainer/createContainerReplica",
            "http://66.135.200.23/myvisionhost/ManagerContainer/replicateContainer", "container=container1" }, //
            { "http://147.102.19.45/myvisionhost/ManagerObject/createObjectReplica",
            "http://66.135.200.23/myvisionhost/ManagerObject/replicateObject", "object=object1" }, //
                                              };
    /***/
    static final long           Period        = 20000;
    private static final String containerName = "test-container";
    private static final String objectName    = "test-object";
    private static final String tenantName    = "ntua";


    /**
     * test case entry point.
     * 
     * @param args
     *            ignored.
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        final UUID appId = UUID.randomUUID();
        System.out.println( "Initializing app (id= " + appId + ")" );
        VisionMonitoring.initialize( "http://10.0.2.111:8080/vismo/Monitoring/push/event", appId );

        while( true ) {
            Thread.sleep( Period );

            final List<Resource> resources = Lists.newArrayList();

            resources.add( new ResourceImpl( "memory", Math.random() * 3096, containerName, objectName, tenantName ) );
            resources.add( new ResourceImpl( "storage", Math.random() * 1024, containerName, objectName, tenantName ) );

            final String[] action = Actions[Math.min( (int) Math.floor( Actions.length * Math.random() ), Actions.length - 1 )];

            final URL src = new URL( action[0] );
            final URL trg = action[1] == null ? null : new URL( action[1] );

            final Map<String, String> params = Maps.newHashMap();
            for( int i = 2; i < action.length; ++i ) {
                final String[] parts = action[i].split( "=" );
                params.put( parts[0], parts[1] );
            }

            VisionMonitoring.instance().log( src, trg, "foo", "bar",
                                             XDasEventType.XDAS_AE_MODIFY_DATA_ITEM_ASSOC_CONTEXT.getEventCode(),
                                             XdasOutcomes.XDAS_OUT_SUCCESS.getOutcomeCode(), params, resources );

            System.out.println( "action saved." );
        }
    }
}
