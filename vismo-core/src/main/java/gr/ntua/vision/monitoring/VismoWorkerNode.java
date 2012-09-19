package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;

import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class VismoWorkerNode extends AbstractVismoCloudElement {
    /***/
    private static final Logger log = LoggerFactory.getLogger(VismoWorkerNode.class);


    /**
     * Constructor.
     * 
     * @param vminfo
     * @param sink
     * @param sources
     * @throws SocketException
     */
    public VismoWorkerNode(final VMInfo vminfo, final EventSink sink, final BasicEventSource... sources) throws SocketException {
        super(vminfo, sink, sources);
    }


    /**
     * @see gr.ntua.vision.monitoring.EventListener#receive(gr.ntua.vision.monitoring.events.Event)
     */
    @Override
    public void receive(final Event e) {
        doYourThing(e);
    }


    /**
     * @see gr.ntua.vision.monitoring.AbstractVismoCloudElement#log()
     */
    @Override
    protected Logger log() {
        return log;
    }


    /**
     * @param e
     */
    private void doYourThing(final Event e) {
        // TODO: maybe in another thread?
        send(e);
    }
}
