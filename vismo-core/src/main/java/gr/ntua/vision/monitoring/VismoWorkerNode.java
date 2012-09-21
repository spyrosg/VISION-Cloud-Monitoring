package gr.ntua.vision.monitoring;

import gr.ntua.vision.monitoring.events.Event;
import gr.ntua.vision.monitoring.events.VismoEventFactory;
import gr.ntua.vision.monitoring.sinks.BasicEventSink;
import gr.ntua.vision.monitoring.sources.BasicEventSource;
import gr.ntua.vision.monitoring.zmq.ZMQSockets;

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
     * @param service
     */
    public VismoWorkerNode(final VismoService service) {
        super(service);
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


    /**
     * @see gr.ntua.vision.monitoring.VismoCloudElement#setup(gr.ntua.vision.monitoring.VismoConfiguration,
     *      gr.ntua.vision.monitoring.zmq.ZMQSockets)
     */
    @Override
    public void setup(VismoConfiguration conf, ZMQSockets zmq) {
        final BasicEventSource source = getSource(zmq, conf.getProducersPoint());

        attach(source);
        
        final BasicEventSink sink = new BasicEventSink(zmq.newConnectedPushSocket(conf.get));
    }


    /**
     * @param zmq
     * @param addr
     * @return
     */
    private static BasicEventSource getSource(final ZMQSockets zmq, final String addr) {
        return new BasicEventSource(new VismoEventFactory(), zmq.newBoundPubSocket(addr));
    }
}
