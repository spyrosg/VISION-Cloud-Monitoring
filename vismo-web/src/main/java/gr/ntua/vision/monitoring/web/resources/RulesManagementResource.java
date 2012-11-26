package gr.ntua.vision.monitoring.web.resources;

import java.util.HashMap;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("/")
public class RulesManagementResource {
    private static final HashMap<Integer, String> catalog = new HashMap<Integer, String>();
    private static final Logger      log          = LoggerFactory.getLogger(RulesManagementResource.class);

    @GET
    @Produces("text/plain")
    @Path("rules/{id}")
    public String RulesConfigurationGet(@PathParam("id") Integer id){                
        log.info("requesting rule: {}", id + ".");
        return catalog.get(id);
    }

    @PUT
    @Path("rules/{name}/{period}/{desc}")
    @Consumes("text/plain")
    public String RulesConfigurationPut(
            @PathParam("name") String name,
            @PathParam("period") Integer period,
            @PathParam("desc") String desc)
    {    
        log.info("configuring new rule: {}", name, ".");
        catalog.put(catalog.size()+1, name+":"+period+":"+desc);
        //return the object that was inserted
        return catalog.get(catalog.size());

    }

    @DELETE
    @Produces("text/plain")
    @Path("rules/{id}")
    public String RulesConfigurationDelete(
            @PathParam("id") Integer id)
    {  
        log.info("removed rule: {}", id, ".");
        //catalog.remove(id);
        return catalog.get(catalog.size());
    }

}
