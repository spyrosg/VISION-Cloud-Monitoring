package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.ext.catalog.LocalCatalogFactory;

import java.util.List;

import javax.servlet.ServletContext;
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
	/** instance used by the web application. */
	private static VismoCtxListener	instance	= null;
	/** running instances. */
	private final List<Monitoring>	instances	= Lists.newArrayList();
	/** the servlet context */
	private ServletContext			ctx;


	/**
	 * Default constructor.
	 */
	public VismoCtxListener()
	{
		instance = this;
	}


	/**
	 * get the instance used by the web application.
	 * 
	 * @return the instance.
	 */
	public static VismoCtxListener instance()
	{
		return instance;
	}


	/**
	 * check if an instance of the given monitoring type is alive.
	 * 
	 * @param type
	 *            the monitoring instance type.
	 * @return <code>true</code> if and only if an instance of the given monitoring type is alive.
	 */
	public boolean isAlive(Class< ? extends Monitoring> type)
	{
		for( Monitoring mtr : instances )
			if( type.isInstance( mtr ) ) //
				return true;
		return false;
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
			ctx = event.getServletContext();
			String instances_t = ctx.getInitParameter( "instance.type" );
			ctx.setAttribute( "lcl-store", LocalCatalogFactory.localCatalogInstance() );

			String[] types = instances_t.split( ";" );

			for( String instance_t : types )
			{
				@SuppressWarnings("unchecked")
				Class< ? extends Monitoring> mtr_t = (Class< ? extends Monitoring>) Class.forName( instance_t );

				launch( mtr_t );
			}
		}
		catch( Exception x )
		{
			x.printStackTrace();
		}
	}


	/**
	 * launch an instance.
	 * 
	 * @param mtr_t
	 *            the monitoring instance type.
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	public void launch(Class< ? extends Monitoring> mtr_t) throws IllegalAccessException, NoSuchFieldException
	{
		Monitoring instance = (Monitoring) mtr_t.getField( "instance" ).get( null );
		instance.launch( ctx );
		instances.add( instance );
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
