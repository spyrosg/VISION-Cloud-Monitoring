package gr.ntua.vision.monitoring.web;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import com.yammer.dropwizard.config.Configuration;


/**
 * 
 */
public class VismoServiceConfiguration extends Configuration {
    /***/
    @NotEmpty
    @JsonProperty
    private String address;


    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }
}
