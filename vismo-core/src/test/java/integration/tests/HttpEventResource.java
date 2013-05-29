package integration.tests;

import java.net.InetAddress;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import java.net.URI;
import java.net.UnknownHostException;
import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.events.VismoEventFactory;


import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.spi.container.ContainerResponse;








@Path("events")
public class HttpEventResource{
	
	VismoEventFactory e = new VismoEventFactory();
	
	@PUT	
	public Response putEvent(String x){
		try{
			MonitoringEvent monev= e.createEvent(x);
			try{
				InetAddress IP = monev.originatingIP();
			    if (IP == null){
				return Response.status(400).entity("No IP").build();
			    }
			}catch(UnknownHostException e){
				return Response.status(400).entity(e.getMessage()).build();
			}
			String service = monev.originatingService();
			if (service == null){
				return Response.status(400).entity("no service").build();
			}
			Long timest = monev.timestamp();
			if (timest == null){
				return Response.status(400).entity("no timestamp").build();
			}
			String topic = monev.topic();
			if (topic == null){
				return Response.status(400).entity("no topic").build();
			}
		}catch(java.lang.Error e){
		    return Response.status(400).entity(e.getMessage()).build();
		}
		return Response.created(URI.create("/")).build();
		
	}
}
	