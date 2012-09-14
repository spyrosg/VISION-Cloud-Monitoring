package gr.ntua.vision.monitoring;

/**
 *
 */
public class MainMain {
    /***/
    private final VismoCloudElementFactory factory = new VismoCloudElementFactory();


    /**
     * 
     */
    public void start() {
        final VismoCloudElement elem = hostIsClusterLeader() ? factory.createVismoClusterNode() : factory.createVismoNode();

        elem.start();
    }


    /**
     * @return
     */
    private boolean hostIsClusterLeader() {
        // TODO Auto-generated method stub
        return false;
    }
}
