package gr.ntua.vision.monitoring.web.resources;

import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("/")
public class RulesManagementService {
    
    HashMap<Integer, String>     Rules = new HashMap<Integer, String>();
    private static final Logger log   = LoggerFactory.getLogger(RulesManagementService.class);

    @GET
    @Path("/rules/{ruleId}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String RulesConfigurationGet(@PathParam("ruleId") Integer ruleId) {
        log.info("requesting view rule: {}", ruleId + ".");
        return Rules.get(ruleId);
    }

    @PUT
    @Path("/rules/{ruleName:rulePeriod:ruleDesc}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public void RulesConfigurationPut(
            @PathParam("ruleName") String ruleName,
            @PathParam("rulePeriod")String rulePeriod,
            @PathParam("ruleDesc")String ruleDesc){
                       
        log.info("configuring new rule: {}", ruleName, ".");        
        Rules.put(Rules.size(), ruleName+":"+rulePeriod+":"+ruleDesc);
    }       
    
}
