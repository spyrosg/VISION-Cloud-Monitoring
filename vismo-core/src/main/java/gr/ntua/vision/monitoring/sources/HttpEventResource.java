package gr.ntua.vision.monitoring.sources;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.events.VismoEventFactory;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;



@Path("events")
public class HttpEventResource implements EventSource {
	
	VismoEventFactory factory = new VismoEventFactory();
	
	@PUT	
	public Response putEvent(String x){
		try{
			MonitoringEvent monev= factory.createEvent(x);
			try{
				InetAddress IP = monev.originatingIP();
			    if (IP == null){
				return Response.status(400).entity("No originating IP").build();
			    }
			}catch(UnknownHostException e){
				return Response.status(400).entity(e.getMessage()).build();
			}
			String service = monev.originatingService();
			if (service == null){
				return Response.status(400).entity("field originating-service required").build();
			}
			Long timest = monev.timestamp();
			if (timest == null){
				return Response.status(400).entity("field timestamp required").build();
			}
			String topic = monev.topic();
			if (topic == null){
				return Response.status(400).entity("field topic required").build();
			}
		}catch(java.lang.Error e){
		    return Response.status(400).entity(e.getMessage()).build();
		}
		return Response.created(URI.create("/")).build();
		
	}

	@Override
	public void add(EventSourceListener listener) {
		// TODO Auto-generated method stub
		
	}
}
	