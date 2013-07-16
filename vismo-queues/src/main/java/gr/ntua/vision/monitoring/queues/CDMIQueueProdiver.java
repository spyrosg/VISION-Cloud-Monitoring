package gr.ntua.vision.monitoring.queues;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;


/**
 * This is used to provide serialization/deserialization for the CDMI Queue media type.
 */
@Provider
@Consumes(CDMIQueueMediaTypes.APPLICATION_CDMI_QUEUE)
@Produces(CDMIQueueMediaTypes.APPLICATION_CDMI_QUEUE)
public class CDMIQueueProdiver extends JacksonJsonProvider {
    /**
     * Constructor.
     */
    public CDMIQueueProdiver() {
        super();
    }


    /**
     * @see org.codehaus.jackson.jaxrs.JacksonJsonProvider#isReadable(java.lang.Class, java.lang.reflect.Type,
     *      java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
     */
    @Override
    public boolean isReadable(final Class< ? > type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType) {
        return isCDMIQueueType(mediaType) || super.isReadable(type, genericType, annotations, mediaType);
    }


    /**
     * @see org.codehaus.jackson.jaxrs.JacksonJsonProvider#isWriteable(java.lang.Class, java.lang.reflect.Type,
     *      java.lang.annotation.Annotation[], javax.ws.rs.core.MediaType)
     */
    @Override
    public boolean isWriteable(final Class< ? > type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType) {
        return isCDMIQueueType(mediaType) || super.isWriteable(type, genericType, annotations, mediaType);
    }


    /**
     * Check whether the media type passed is compatible to the CDMI Queue media type.
     * 
     * @param mediaType
     * @return <code>true</code> iff the media type is a CDMI Queue media type.
     */
    private static boolean isCDMIQueueType(final MediaType mediaType) {
        return CDMIQueueMediaTypes.APPLICATION_CDMI_QUEUE_TYPE.equals(mediaType);
    }
}
