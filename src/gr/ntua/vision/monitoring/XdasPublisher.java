package gr.ntua.vision.monitoring;

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
	private Connection			connection;
	/** The session. */
	private Session				session;
	/** The publisher. */
	private MessageProducer		publisher;
	/** The topic. */
	private Topic				topic;
	/** The topicname. */
	private String				topicname	= "vision.xdas";
	/** The url. */
	private String				url			= "tcp://10.0.2.71:61616";
	/** disable activemq flag */
	private boolean				disable;


	/**
	 * c/tor.
	 */
	public XdasPublisher()
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
	public void stop() throws JMSException
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
