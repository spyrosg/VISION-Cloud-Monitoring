package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.rules.parser.RuleSpec;

import com.google.common.base.Predicate;


/**
 * The event matcher.
 */
public class EventMatcher
{
	/** The associated rule. */
	public final RuleSpec				rule;
	/**
	 * the event matching expression. The first dimension binds its contents with a logical AND and the second dimension with a
	 * logical OR.
	 */
	private final Predicate<Event>[][]	andorXpr;


	/**
	 * c/tor.
	 * 
	 * @param rule
	 *            the associated rule.
	 * @param andorXpr
	 *            the event matching expression. The first dimension binds its contents with a logical AND and the second
	 *            dimension with a logical OR.
	 */
	EventMatcher(RuleSpec rule, Predicate<Event>[][] andorXpr)
	{
		this.rule = rule;
		this.andorXpr = andorXpr;
	}


	/**
	 * check if the given event matches the stored expression.
	 * 
	 * @param event
	 *            the event.
	 * @return <code>true</code> if and only if the given event matches the stored expression.
	 */
	public boolean matches(Event event)
	{
		for( Predicate<Event>[] ored : andorXpr )
		{
			boolean match = false;
			for( Predicate<Event> prd : ored )
				if( prd.apply( event ) )
				{
					match = true;
					break;
				}
			if( !match ) return false;
		}
		return true;
	}
}
