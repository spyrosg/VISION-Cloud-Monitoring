package gr.ntua.vision.monitoring;

/**
 *
 */
public class VismoCloudElementFactory {
    /***/
    private final VismoConfiguration conf;


    /**
     * @param conf
     */
    private VismoCloudElementFactory(final VismoConfiguration conf) {
        this.conf = conf;
    }


    /**
     * @return
     */
    public VismoClusterNode createVismoClusterNode() {
        // TODO
        return null;
    }


    /**
     * @return
     */
    public OldVismoNode createVismoNode() {
        return null;
    }
}
