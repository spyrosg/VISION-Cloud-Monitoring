package gr.ntua.vision.monitoring;

import java.io.IOException;
import java.util.Properties;


/**
 * 
 */
public class VismoConfiguration extends PropertiesConfiguration {
    /***/
    private static final String CONSUMERS_POINT_PROPERTY = "consumers.point";
    /***/
    private static final String PRODUCERS_POINT_PROPERTY = "producers.point";
    /***/
    private static final String UDP_PORT_PROPERTY        = "udp.port";


    /**
     * Constructor.
     * 
     * @param props
     *            the properties object.
     */
    public VismoConfiguration(final Properties props) {
        super(props);
    }


    /**
     * Constructor.
     * 
     * @param filename
     *            the file to load the properties from.
     * @throws IOException
     */
    public VismoConfiguration(final String filename) throws IOException {
        super(filename);
    }


    /**
     * @return the consumers port.
     */
    public String getConsumersPoint() {
        return get(CONSUMERS_POINT_PROPERTY);
    }


    /**
     * @return the producers port.
     */
    public String getProducersPoint() {
        return get(PRODUCERS_POINT_PROPERTY);
    }


    /**
     * @return the udp server port.
     */
    public int getUDPPort() {
        return Integer.valueOf(get(UDP_PORT_PROPERTY));
    }
}
