package gr.ntua.vision.monitoring.sources;

import gr.ntua.vision.monitoring.events.EventFactory;
import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.rules.VismoRulesEngine;
import gr.ntua.vision.monitoring.sinks.EventSink;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;




/**
 * 
 */
@Path("events")
public class HttpEventResource implements EventSource {
   
	

	

    /***/
    private final EventFactory factory;
    /***/
    private EventSource source;
   
    /** the listeners lists. */
    private final ArrayList<EventSourceListener> listeners = new ArrayList<EventSourceListener>();
    
    private EventSourceListener listener;
    
    /***/
    private final ArrayList<EventSink> sinks = new ArrayList<EventSink>();
    
    
    /**
     * Constructor
     * 
     * @param factory
     */
    public HttpEventResource (final EventFactory factory){
    	this.factory = factory;
    }
   
	
    
   

    /**
     * @see gr.ntua.vision.monitoring.sources.EventSource#add(gr.ntua.vision.monitoring.sources.EventSourceListener)
     */
    @Override
    public void add(final EventSourceListener listener) {
        source.add(listener);
        listeners.add(listener);
    }


    /**
     * @param body
     * @return
     */
    @PUT
    public Response putEvent(final String body) {
        try {
            final MonitoringEvent monev = factory.createEvent(body);
            eventValidation(monev);
        } catch (final java.lang.Error e) {
            return Response.status(400).entity(e.getMessage()).build();
        }

        return Response.created(URI.create("/")).build();
        
    }
    
    /**
     * Validate event's fields
     * 
     * @param the event
     * 
     * @return
     */
    public Response eventValidation(MonitoringEvent ev){
            validateIP(ev);
            validateOriginatingService(ev);
            validateEventTimestamp(ev);
            validateEventTopic(ev);
            
            return Response.created(URI.create("/")).build();
    }
    
    /**
     * Validating Event IP
     * 
     * @param the event
     * 
     * @return
     */
    public Response validateIP(MonitoringEvent ev){
    	try {
            final InetAddress IP = ev.originatingIP();
            if (IP == null)
                return Response.status(400).entity("No originating IP").build();
        } catch (final UnknownHostException e) {
            return Response.status(400).entity(e.getMessage()).build();
        }
    	
    	return Response.created(URI.create("/")).build();
    }
    
    /**
     * Validate Event's Originating Service 
     * 
     * @param the event 
     * 
     * @return
     */
    public Response validateOriginatingService(MonitoringEvent ev){
    	final String service = ev.originatingService();
        if (service == null)
            return Response.status(400).entity("field originating-service required").build();
       
        return Response.created(URI.create("/")).build();
    }
    
    /**
     * Validate Event's Timestamp
     * 
     * @param the event 
     * 
     * @return
     */
    public Response validateEventTimestamp(MonitoringEvent ev){
    	final Long timest = ev.timestamp();
        if (timest == null)
            return Response.status(400).entity("field timestamp required").build();
    	
        return Response.created(URI.create("/")).build();
    }
    
    /**
     * Validate Event's topic
     * 
     * @param the event 
     * 
     * @return
     */
    public Response validateEventTopic(MonitoringEvent ev){
    	final String topic = ev.topic();
        if (topic == null)
            return Response.status(400).entity("field topic required").build();
        
        return Response.created(URI.create("/")).build();
    }
    
    public Response receiveEvent(MonitoringEvent ev){
    	listener.receive(ev);
    	VismoRulesEngine engine = new VismoRulesEngine();
    	engine.appendSinks(sinks);
    	if (sinks != null){
    		return Response.created(URI.create("/")).build();
    	}
    	return Response.status(400).entity("VismoRulesEngine didn't receive the event").build();
    }
}
