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
public abstract class XdasPublisher
{
	/** the logger. */
	@SuppressWarnings("all")
	private static final Logger	log			= Logger.getLogger( XdasPublisher.class );
	/** The connection. */
	private static Connection			connection;
	/** The session. */
	private static Session				session;
	/** The publisher. */
	private static MessageProducer		publisher;
	/** The topic. */
	private static Topic				topic;
	/** The topicname. */
	private static String				topicname	= "vision.xdas";
	/** The url. */
	private static String		url			= "tcp://10.0.1.71:61616";
	/** disable activemq flag */
	private static boolean				disable;


	/**
	 * c/tor.
	 */
	public XdasPublisher()
	{
		init();
	}


	/**
	 * @return the url
	 */
	public static String getUrl()
	{
		return url;
	}


	/**
	 * @param url
	 *            the url to set
	 * @throws JMSException 
	 */
	public static void setUrl(String url) throws JMSException
	{
		stop();
		XdasPublisher.url = url;
		init();
	}


	/**
	 * initialize the publisher.
	 */
	public static void init()
	{
		try
		{
			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory( url );
			connection = factory.createConnection();
			session = connection.createSession( false, Session.AUTO_ACKNOWLEDGE );
			topic = session.createTopic( topicname );

			publisher = session.createProducer( topic );
			publisher.setDeliveryMode( DeliveryMode.NON_PERSISTENT );
			disable = false;
		}
		catch( JMSException x )
		{
			log.error( "ActiveMQ can't be initialized", x );
			log.warn( "XDAS event publishing disabled." );
			disable = true;
		}
	}


	/**
	 * stop the publisher.
	 * 
	 * @throws JMSException
	 */
	public static void stop() throws JMSException
	{
		if( disable ) return;

		connection.stop();
		connection.close();
	}


	/**
	 * Send XDAS.
	 * 
	 * @param xdasmessage
	 *            the XDAS message
	 * @throws Exception
	 *             the exception
	 */
	protected void sendXdas(String xdasmessage) throws Exception
	{
		if( disable ) return;

		TextMessage msg = session.createTextMessage();

		msg.setText( xdasmessage );
		publisher.send( msg );
	}
}
