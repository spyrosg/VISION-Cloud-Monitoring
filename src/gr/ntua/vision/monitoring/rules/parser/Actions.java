package gr.ntua.vision.monitoring.rules.parser;

import java.util.Arrays;
import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Terminals;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Tuple3;

import com.google.common.collect.Lists;


/**
 * The possible rule actions.
 */
public enum Actions
{
	/***/
	Store(String.class, Integer.class, Integer.class, Long.class),
	/***/
	PushAsIs(String.class),
	/***/
	PushAggregated(String.class, Integer.class, Integer.class, Long.class),
	/***/
	;

	/** the argument types. */
	private final Class< ? >[]	arg_types;


	/**
	 * c/tor.
	 * 
	 * @param arg_types
	 */
	private Actions(Class< ? >... arg_types)
	{
		this.arg_types = arg_types;
	}


	/**
	 * get the arguments parser for this.
	 * 
	 * @param comma
	 *            the parser for the comma character.
	 * @return the parser.
	 */
	public Parser<Object[]> argumentParser(Parser< ? > comma)
	{
		Parser<List<Object>> next = null;

		for( int i = arg_types.length - 1; i >= 0; --i )
		{
			Parser<List<Object>> argParser = mkParser( arg_types[i] ).map( new Map<Object, List<Object>>() {
				@Override
				public List<Object> map(Object arg0)
				{
					return Arrays.asList( arg0 );
				}
			} );

			if( next != null )
				next = Parsers.tuple( argParser, comma, next )
						.map( new Map<Tuple3<List<Object>, Object, List<Object>>, List<Object>>() {
							@Override
							public List<Object> map(Tuple3<List<Object>, Object, List<Object>> arg0)
							{
								List<Object> list = Lists.newArrayList( arg0.a );
								list.addAll( arg0.c );
								return list;
							}
						} );
			else next = argParser;
		}

		if( next == null ) return Parsers.always();

		return next.map( new Map<List<Object>, Object[]>() {
			@Override
			public Object[] map(List<Object> arg0)
			{
				return arg0.toArray( new Object[arg0.size()] );
			}
		} );
	}


	/**
	 * create a parser for the given type.
	 * 
	 * @param type
	 *            the type to parse.
	 * @return the parser.
	 */
	private Parser< ? > mkParser(Class< ? > type)
	{
		if( type == String.class ) //
			return Terminals.StringLiteral.PARSER;

		if( type == Integer.class ) //
			return Terminals.DecimalLiteral.PARSER.map( new Map<String, Integer>() {
				@Override
				public Integer map(String arg0)
				{
					return Integer.parseInt( arg0 );
				}
			} );

		if( type == Long.class ) //
			return Terminals.DecimalLiteral.PARSER.map( new Map<String, Long>() {
				@Override
				public Long map(String arg0)
				{
					return Long.parseLong( arg0 );
				}
			} );

		throw new AssertionError( "unreachable" );
	}
}
