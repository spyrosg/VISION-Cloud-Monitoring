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
	 * shutdown the application. It is illegal to call this more than once.
	 */
	public void shutdown();
}
