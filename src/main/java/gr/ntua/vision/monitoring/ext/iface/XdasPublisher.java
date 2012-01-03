package gr.ntua.vision.monitoring.ext.iface;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;


/**
 * This is responsible for publishing XDAS messages to their receiving agents.
 */
public abstract class XdasPublisher {
    /** The connection. */
    private static Connection      connection;
    /** disable activemq flag */
    private static boolean         disable;
    /** the logger. */
    @SuppressWarnings("all")
    private static final Logger    log       = Logger.getLogger( XdasPublisher.class );
    /** The publisher. */
    private static MessageProducer publisher;
    /** The session. */
    private static Session         session;
    /** The topic. */
    private static Topic           topic;
    /** The topicname. */
    private static String          topicname = "vision.xdas";
    /** The url. */
    private static String          url       = "tcp://10.0.1.71:61616";


    /**
     * c/tor.
     */
    public XdasPublisher() {
        init();
    }


    /**
     * Send XDAS.
     * 
     * @param xdasmessage
     *            the XDAS message
     * @throws Exception
     *             the exception
     */
    protected void sendXdas(final String xdasmessage) throws Exception {
        if( disable )
            return;

        final TextMessage msg = session.createTextMessage();

        msg.setText( xdasmessage );
        publisher.send( msg );
    }


    /**
     * @return the url
     */
    public static String getUrl() {
        return url;
    }


    /**
     * initialize the publisher.
     */
    public static void init() {
        if( url != null && connection == null ) //
            try {
                final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory( url );
                connection = factory.createConnection();
                session = connection.createSession( false, Session.AUTO_ACKNOWLEDGE );
                topic = session.createTopic( topicname );

                publisher = session.createProducer( topic );
                publisher.setDeliveryMode( DeliveryMode.NON_PERSISTENT );
                disable = false;
            } catch( final JMSException x ) {
                log.error( "ActiveMQ can't be initialized", x );
                log.warn( "XDAS event publishing disabled." );
                disable = true;
            }
    }


    /**
     * @param url
     *            the url to set
     * @throws JMSException
     */
    public static void setUrl(final String url) throws JMSException {
        stop();
        XdasPublisher.url = url;
        init();
    }


    /**
     * stop the publisher.
     * 
     * @throws JMSException
     */
    public static void stop() throws JMSException {
        if( disable || connection == null )
            return;

        connection.stop();
        connection.close();
    }
}
