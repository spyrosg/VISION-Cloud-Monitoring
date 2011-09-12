package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.ext.local.LocalCatalogFactory;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;


/**
 * Application Lifecycle Listener implementation class VismoCtxListener
 */
public class VismoCtxListener implements ServletContextListener
{
	/** the logger. */
	@SuppressWarnings("all")
	private static final Logger		log			= Logger.getLogger( VismoCtxListener.class );
	/** running instances. */
	private final List<Monitoring>	instances	= Lists.newArrayList();


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
			String instances_t = event.getServletContext().getInitParameter( "instance.type" );

			String[] types = instances_t.split( ";" );

			for( String instance_t : types )
			{
				@SuppressWarnings("unchecked")
				Class< ? extends Monitoring> mtr_t = (Class< ? extends Monitoring>) Class.forName( instance_t );

				Monitoring instance = (Monitoring) mtr_t.getField( "instance" ).get( null );

				instance.launch( event.getServletContext() );
				event.getServletContext().setAttribute( "lcl-store", LocalCatalogFactory.localCatalogInstance() );

				instances.add( instance );
			}
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

		while( !instances.isEmpty() )
			instances.remove( 0 ).shutdown();
	}
}
