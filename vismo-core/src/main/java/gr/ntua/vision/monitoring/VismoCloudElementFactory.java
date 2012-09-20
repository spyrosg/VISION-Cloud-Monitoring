package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.sinks.EventSink;
import gr.ntua.vision.monitoring.sources.BasicEventSource;

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
     * @throws SocketException
     */
    public VismoClusterHead createVismoClusterHeadNode(final EventSink sink, final List<BasicEventSource> sources)
            throws SocketException {
        return new VismoClusterHead(vminfo, sink, sources);
    }


    /**
     * @param source
     * @param sink
     * @return
     * @throws SocketException
     */
    public VismoWorkerNode createVismoWorkerNode(final BasicEventSource source, final EventSink sink) throws SocketException {
        return new VismoWorkerNode(vminfo, sink, source);
    }
}
