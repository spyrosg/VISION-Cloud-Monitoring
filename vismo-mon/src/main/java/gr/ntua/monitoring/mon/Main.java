package gr.ntua.monitoring.mon;

import gr.ntua.vision.monitoring.VismoConfiguration;

import java.io.IOException;


/**
 * 
 */
public class Main {
    /**
     * @param args
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {
        final VismoConfiguration conf = new VismoConfiguration(args[0]);
        final VismoGroupService service = new VismoGroupServiceFactory(conf).build();

        service.start();
    }
}
