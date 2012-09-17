package gr.ntua.vision.monitoring;

import java.net.SocketException;
import java.util.List;


/**
 *
 */
public class VismoCloudElementFactory {
    /***/
    private final VMInfo vminfo;


    /**
     * Constructor.
     * 
     * @param vminfo
     */
    public VismoCloudElementFactory(final VMInfo vminfo) {
        this.vminfo = vminfo;
    }


    /**
     * @param sink
     * @param sources
     * @return
     */
    @SuppressWarnings("static-method")
    public VismoClusterHead createVismoClusterHeadNode(final EventSink sink, final List<EventSource> sources) {
        return new VismoClusterHead(sink, sources);
    }


    /**
     * @param source
     * @param sink
     * @return
     * @throws SocketException
     */
    public VismoWorkerNode createVismoWorkerNode(final EventSource source, final EventSink sink) throws SocketException {
        return new VismoWorkerNode(vminfo, source, sink);
    }
}
