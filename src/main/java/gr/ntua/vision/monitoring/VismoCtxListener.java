package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.cloud.CloudMonitoring;
import gr.ntua.vision.monitoring.cluster.ClusterMonitoring;
import gr.ntua.vision.monitoring.ext.iface.XdasPublisher;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.jms.JMSException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;


/**
 * Application Lifecycle Listener implementation class VismoCtxListener
 */
public class VismoCtxListener implements ServletContextListener {
    /** instance used by the web application. */
    private static VismoCtxListener instance  = null;
    /** the logger. */
    @SuppressWarnings("all")
    private static final Logger     log       = Logger.getLogger( VismoCtxListener.class );
    /** the servlet context */
    private ServletContext          ctx;
    /** running instances. */
    private final List<Monitoring>  instances = Lists.newArrayList();


    /**
     * Default constructor.
     */
    public VismoCtxListener() {
        instance = this;
    }


    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        log.debug( "ctx destroy" );

        while( !instances.isEmpty() )
            instances.remove( 0 ).shutdown();

        try {
            XdasPublisher.stop();
        } catch( final JMSException e ) {
            e.printStackTrace();
        }
    }


    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    @Override
    public void contextInitialized(final ServletContextEvent event) {
        try {
            log.debug( "ctx init" );
            ctx = event.getServletContext();

            final InetAddress[] cloudAddresses = InetAddress.getAllByName( ctx.getInitParameter( "cloud.instance.host" ) );

            boolean haveCloudAddress = false;
            final Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for( final NetworkInterface netint : Collections.list( nets ) ) {
                final Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                for( final InetAddress inetAddress : Collections.list( inetAddresses ) )
                    if( inetAddress.equals( cloudAddresses ) ) {
                        haveCloudAddress = true;
                        break;
                    }
                if( haveCloudAddress )
                    break;
            }

            if( haveCloudAddress ) //
                launch( CloudMonitoring.class );

            launch( ClusterMonitoring.class );
        } catch( final Exception x ) {
            x.printStackTrace();
        }
    }


    /**
     * check if an instance of the given monitoring type is alive.
     * 
     * @param type
     *            the monitoring instance type.
     * @return <code>true</code> if and only if an instance of the given monitoring type is alive.
     */
    public boolean isAlive(final Class< ? extends Monitoring> type) {
        for( final Monitoring mtr : instances )
            if( type.isInstance( mtr ) ) //
                return true;
        return false;
    }


    /**
     * launch an instance.
     * 
     * @param mtr_t
     *            the monitoring instance type.
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public void launch(final Class< ? extends Monitoring> mtr_t) throws IllegalAccessException, NoSuchFieldException {
        final Monitoring instance = (Monitoring) mtr_t.getField( "instance" ).get( null );
        instance.launch( ctx );
        instances.add( instance );
    }


    /**
     * shutdown any instance of the given monitoring type.
     * 
     * @param type
     *            the monitoring instance type.
     */
    public void shutdown(final Class< ? extends Monitoring> type) {
        for( final Monitoring mtr : instances )
            if( type.isInstance( mtr ) ) {
                instances.remove( mtr );
                mtr.shutdown();
                return;
            }
    }


    /**
     * get the instance used by the web application.
     * 
     * @return the instance.
     */
    public static VismoCtxListener instance() {
        return instance;
    }
}
