package gr.ntua.vision.monitoring.ext.iface;

import gr.ntua.vision.monitoring.cloud.CloudMonitoring;
import gr.ntua.vision.monitoring.rules.parser.RuleParser;
import gr.ntua.vision.monitoring.rules.parser.RuleSpec;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONStringer;
import org.json.JSONWriter;


/**
 * This contains the cloud REST interface entries.
 */
@Path("/cloud")
public class CloudInterface
{
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
		log.debug( "REST: setCloudrMonitoringParameter('" + name + "')" );
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
		if( CloudMonitoring.instance.isInstanceAlive() )
		{
			log.debug( "REST: registerAggregationRule(code size: " + rule.length() + " chars)" );

			compiled = RuleParser.instance.ruleParser.parse( rule );

			log.debug( "REST: registerAggregationRule() compiled rule: " + compiled.name + " :: " + compiled.id );

			CloudMonitoring.instance.ruleEngine.register( compiled );
		}

		JSONWriter wr = new JSONStringer().object();
		wr.key( "status" ).value( compiled == null ? "failed" : "ok" );
		if( compiled != null ) //
			wr.key( "id" ).value( compiled.id.toString() );
		return wr.endObject().toString();
	}
}
