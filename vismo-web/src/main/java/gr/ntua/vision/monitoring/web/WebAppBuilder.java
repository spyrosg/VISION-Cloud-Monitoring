package gr.ntua.vision.monitoring.web;

import java.util.HashSet;

import javax.ws.rs.core.Application;

import com.sun.jersey.api.container.filter.LoggingFilter;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;


/**
 * 
 */
public class WebAppBuilder {
    /***/
    private final HashSet<Class< ? >> classes;
    /***/
    private final HashSet<Object>     resources;


    /**
     * Constructor.
     */
    public WebAppBuilder() {
        this.resources = new HashSet<Object>();
        this.classes = new HashSet<Class< ? >>();
    }


    /**
     * @param c
     * @return <code>this</code>.
     */
    public WebAppBuilder addProvide(final Class< ? > c) {
        classes.add(c);

        return this;
    }


    /**
     * @param o
     * @return <code>this</code>.
     */
    public WebAppBuilder addResource(final Object o) {
        resources.add(o);

        return this;
    }


    /**
     * @return a configured {@link Application} object.
     */
    public Application build() {
        final DefaultResourceConfig rc = new DefaultResourceConfig();

        // NOTE: debug help for requests/responses
        rc.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS, new LoggingFilter());
        rc.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, new LoggingFilter());

        // NOTE: this is used to automagically serialize/deserialize pojos in requests/responses from/to json
        rc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, true);
        rc.getSingletons().addAll(resources);
        rc.getClasses().addAll(classes);

        return rc;
    }


    /**
     * Build an application compromised of resources <em>only</em>.
     * 
     * @param resources
     *            the resources.
     * @return an {@link Application} object.
     */
    public static Application buildFrom(final Object... resources) {
        final WebAppBuilder builder = new WebAppBuilder();

        for (final Object resource : resources)
            builder.addResource(resource);

        return builder.build();
    }
}
