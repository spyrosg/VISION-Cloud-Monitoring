package gr.ntua.vision.monitoring;

import javax.servlet.ServletContext;


/**
 * Monitoring instance API.
 */
public interface Monitoring
{
	/**
	 * launch the application.
	 * 
	 * @param ctx
	 *            the servlet context.
	 */
	public void launch(ServletContext ctx);


	/**
	 * check if the instance is {@link #launch(ServletContext)}ed.
	 * 
	 * @return <code>true</code> if and only if the instance is {@link #launch(ServletContext)}ed.
	 */
	public boolean isInstanceAlive();


	/**
	 * shutdown the application. It is illegal to call this more than once.
	 */
	public void shutdown();
}
