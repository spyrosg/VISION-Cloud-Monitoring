package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.core.Monitoring;
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
		log.debug( "ctx init" );
		Monitoring.instance.launch( event.getServletContext().getRealPath( "/" ) );
		event.getServletContext().setAttribute( "lcl-store", LocalCatalogFactory.localCatalogInstance() );
	}


	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event)
	{
		log.debug( "ctx destroy" );
		Monitoring.instance.shutdown();
	}
}
