package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.cluster.ClusterMonitoring;
import gr.ntua.vision.monitoring.ext.local.LocalCatalogFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;


/**
 * Application Lifecycle Listener implementation class VismoCtxListener
 */
public class VismoCtxListener implements ServletContextListener
{
	/** the logger. */
	@SuppressWarnings("all")
	private static final Logger	log	= Logger.getLogger( VismoCtxListener.class );


	/**
	 * Default constructor.
	 */
	public VismoCtxListener()
	{
		// NOP
	}


	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event)
	{
		try
		{
			log.debug( "ctx init" );
			String instance_t = event.getServletContext().getInitParameter( "instance.type" );

			@SuppressWarnings("unchecked")
			Class< ? extends Monitoring> mtr_t = (Class< ? extends Monitoring>) Class.forName( instance_t );

			Monitoring instance = (Monitoring) mtr_t.getField( "instance" ).get( null );

			instance.launch( event.getServletContext() );
			event.getServletContext().setAttribute( "lcl-store", LocalCatalogFactory.localCatalogInstance() );
		}
		catch( Exception x )
		{
			x.printStackTrace();
		}
	}


	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event)
	{
		log.debug( "ctx destroy" );
		ClusterMonitoring.instance.shutdown();
	}
}
