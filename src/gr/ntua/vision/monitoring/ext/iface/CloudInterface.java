package gr.ntua.vision.monitoring.ext.iface;

import gr.ntua.vision.monitoring.VismoCtxListener;
import gr.ntua.vision.monitoring.cloud.CloudMonitoring;
import gr.ntua.vision.monitoring.ext.catalog.GlobalCatalogFactory;
import gr.ntua.vision.monitoring.rules.parser.RuleParser;
import gr.ntua.vision.monitoring.rules.parser.RuleSpec;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONStringer;
import org.codehaus.jettison.json.JSONWriter;

import com.google.common.base.Function;


/**
 * This contains the cloud REST interface entries.
 */
@Path("/cloud")
public class CloudInterface
{
	/** variables that may be handled. */
	private enum Variables
	{
		/***/
		Alive(new Function<String, Void>() {
			@Override
			public Void apply(String arg0)
			{
				if( Boolean.parseBoolean( arg0 ) )
				{
					if( !VismoCtxListener.instance().isAlive( CloudMonitoring.class ) ) //
						try
						{
							VismoCtxListener.instance().launch( CloudMonitoring.class );
						}
						catch( Throwable x )
						{
							throw new RuntimeException( x );
						}
				}
				else
				{
					if( VismoCtxListener.instance().isAlive( CloudMonitoring.class ) )
						VismoCtxListener.instance().shutdown( CloudMonitoring.class );
				}
				return null;
			}
		}),
		/***/
		GlobalCatalog(new Function<String, Void>() {
			@Override
			public Void apply(String arg0)
			{
				GlobalCatalogFactory.setGlobalURL( arg0 );
				return null;
			}
		}),
		/***/
		LocalCatalogs(new Function<String, Void>() {
			@Override
			public Void apply(String arg0)
			{
				String[] clusters = arg0.split( ";" );
				CloudMonitoring.instance.setClusters( clusters );
				return null;
			}
		}),
		/***/
		;

		/** value handler */
		final Function<String, Void>	handler;


		/**
		 * c/tor.
		 * 
		 * @param handler
		 */
		private Variables(Function<String, Void> handler)
		{
			this.handler = handler;
		}
	}

	/** the logger. */
	@SuppressWarnings("all")
	private static final Logger	log	= Logger.getLogger( CloudInterface.class );


	/**
	 * This operation is used to change the parameter of the cloud wide monitoring component.
	 * 
	 * @param name
	 *            the name of the parameter to change.
	 * @param value
	 *            the value to set to the parameter.
	 * @return the operation's success status.
	 * @throws JSONException
	 */
	@GET
	@Path("/setCloudMonitoringParameter")
	@Produces("application/json")
	public String setCloudMonitoringParameter(@QueryParam("name") String name, @QueryParam("value") String value)
			throws JSONException
	{
		log.debug( "REST: setCloudMonitoringParameter('" + name + "' -> '" + value + "')" );

		if( !name.equals( Variables.Alive.toString() ) && !CloudMonitoring.instance.isInstanceAlive() )
		{
			log.warn( "Cloud instance down, ignoring request" );
			return new JSONStringer().object().key( "status" ).value( "service down" ).endObject().toString();
		}

		Variables var = Variables.valueOf( name );
		if( var != null ) var.handler.apply( value );

		return new JSONStringer().object().key( "status" ).value( "ok" ).endObject().toString();
	}


	/**
	 * This operation is used to register a rule with the Cloud level Monitoring Aggregator module. The rule is provided in the
	 * rule language used by the aggregator as a string.
	 * 
	 * @param rule
	 *            a string containing the rule’s code in the rule language understood by the Aggregator.
	 * @return the registration status. When the registration is successful the rule's ID is also provided.
	 * @throws JSONException
	 */
	@POST
	@Path("/registerAggregationRule")
	@Produces("application/json")
	public String registerAggregationRule(@FormParam("rule") String rule) throws JSONException
	{
		RuleSpec compiled = null;
		String failure_reason = "service down";
		if( CloudMonitoring.instance.isInstanceAlive() )
		{
			log.debug( "REST: registerAggregationRule(code size: " + rule.length() + " chars)" );
			log.debug( "Rule:\n" + rule );

			failure_reason = "compilation error";
			compiled = RuleParser.instance.ruleParser.parse( rule );

			log.debug( "REST: registerAggregationRule() compiled rule: " + compiled.name + " :: " + compiled.id );

			failure_reason = "registration failed";
			CloudMonitoring.instance.ruleEngine.register( compiled );
		}

		JSONWriter wr = new JSONStringer().object();
		wr.key( "status" ).value( compiled == null ? "failed" : "ok" );
		if( compiled != null ) //
			wr.key( "id" ).value( compiled.id.toString() );
		else wr.key( "reason" ).value( failure_reason );
		return wr.endObject().toString();
	}
}
