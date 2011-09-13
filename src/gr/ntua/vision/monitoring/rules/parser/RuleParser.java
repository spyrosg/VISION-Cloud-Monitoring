package gr.ntua.vision.monitoring.rules.parser;

import gr.ntua.vision.monitoring.rules.Actions;

import java.util.Arrays;
import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Tuple3;

import com.google.common.collect.Lists;


/**
 * This is the rule parser. This is a singleton object.
 */
public class RuleParser
{
	/** the single instance of this. */
	public static final RuleParser	instance;
	/** the language operators. */
	private static final String[]	operators		= { "{", "}", "=", "[", "]", ",", ";", "(", ")", "<", ">", "-", "." };
	/** the language keywords. */
	private static final String[]	lang_keywords	= { "rule", "when", "then", "like", "Event" };
	/** the language terminals. */
	private static final Terminals	terminals;

	/** the rule parser. */
	public final Parser<RuleSpec>	ruleParser;
	/** the rule collection parser. */
	public final Parser<RuleSpec[]>	rulesParser;

	static
	{
		String[] keywords = new String[lang_keywords.length + Actions.values().length];
		System.arraycopy( lang_keywords, 0, keywords, 0, lang_keywords.length );

		int n = lang_keywords.length;
		for( Actions action : Actions.values() )
			keywords[n++] = action.toString();

		terminals = Terminals.caseSensitive( operators, keywords );

		// MUST do in the end.
		instance = new RuleParser();
	}


	/**
	 * c/tor.
	 */
	private RuleParser()
	{
		ruleParser = rule().from( tokenizer(), ignored() );
		rulesParser = rules().from( tokenizer(), ignored() );
	}


	/**
	 * get the parser for the ignored parts of the text.
	 * 
	 * @return the parser.
	 */
	private Parser<Void> ignored()
	{
		return Parsers.or( Scanners.JAVA_LINE_COMMENT, Scanners.JAVA_BLOCK_COMMENT, Scanners.WHITESPACES ).skipMany();
	}


	/**
	 * create the tokenizer.
	 * 
	 * @return the tokenizer.
	 */
	private Parser< ? > tokenizer()
	{
		return Parsers.or(	Terminals.DecimalLiteral.TOKENIZER, terminals.tokenizer(), Terminals.Identifier.TOKENIZER,
							Terminals.StringLiteral.DOUBLE_QUOTE_TOKENIZER );
	}


	/**
	 * get a language term.
	 * 
	 * @param names
	 *            the array of term strings.
	 * @return the parser.
	 */
	private Parser< ? > term(final String... names)
	{
		return terminals.token( names );
	}


	/**
	 * get a field-check parser.
	 * 
	 * @return the parser.
	 */
	private Parser<FieldCheck> fieldCheck()
	{
		return Parsers.tuple( //
		Parsers.or( Parsers.tuple( Terminals.Identifier.PARSER, term( "." ), Terminals.Identifier.PARSER )
							.map( new Map<Tuple3<String, Object, String>, String[]>() {
								@Override
								public String[] map(Tuple3<String, Object, String> arg0)
								{
									return new String[] { arg0.a, arg0.c };
								}
							} ), //
					Terminals.Identifier.PARSER.map( new Map<String, String[]>() {
						@Override
						public String[] map(String arg0)
						{
							return new String[] { arg0 };
						}
					} ) //
		), //
		Parsers.or( //
		term( "=" ).map( new Map<Object, Boolean>() {
			@Override
			public Boolean map(Object arg0)
			{
				return true;
			}
		} ), //
		term( "like" ).map( new Map<Object, Boolean>() {
			@Override
			public Boolean map(Object arg0)
			{
				return false;
			}
		} ) ), //
								Terminals.StringLiteral.PARSER )//
				.map( new Map<Tuple3<String[], Boolean, String>, FieldCheck>() {
					@Override
					public FieldCheck map(Tuple3<String[], Boolean, String> arg0)
					{
						return arg0.a.length == 1 ? new FieldCheck( arg0.a[0], arg0.b, arg0.c, false ) : new FieldCheck(
								arg0.a[0], new FieldCheck( arg0.a[1], arg0.b, arg0.c, true ) );
					}
				} );
	}


	/**
	 * parse an event specification.
	 * 
	 * @return the parser.
	 */
	private Parser<FieldCheck[]> eventSpec()
	{
		return term( "Event" ).next(	fieldCheck().sepEndBy1( term( "," ) ).between( term( "(" ), term( ")" ) )
												.map( new Map<List<FieldCheck>, FieldCheck[]>() {
													@Override
													public FieldCheck[] map(List<FieldCheck> arg0)
													{
														return arg0.toArray( new FieldCheck[arg0.size()] );
													}
												} ) );
	}


	/**
	 * parse an action specification.
	 * 
	 * @return the parser.
	 */
	private Parser<ActionSpec> actionSpec()
	{
		List<Parser<ActionSpec>> parsers = Lists.newArrayList();

		for( final Actions action : Actions.values() )
			parsers.add( term( action.toString() )
					.next( argumentParser( action.getArgumentTypes() ).between( term( "(" ), term( ")" ) ) )
					.map( new Map<Object[], ActionSpec>() {
						@Override
						public ActionSpec map(Object[] arg0)
						{
							return new ActionSpec( action, arg0 );
						}
					} ) );

		return Parsers.or( parsers );
	}


	/**
	 * get the arguments parser for this.
	 * 
	 * @param arg_types
	 *            the argument types.
	 * @return the parser.
	 */
	public Parser<Object[]> argumentParser(Class< ? >[] arg_types)
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
				next = Parsers.tuple( argParser, term( "," ), next )
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


	/**
	 * get the rule parser.
	 * 
	 * @return the parser.
	 */
	private Parser<RuleSpec> rule()
	{
		return Parsers.tuple( term( "rule" ).next( Terminals.StringLiteral.PARSER ),
								term( "when" ).next( eventSpec().endBy1( term( ";" ) ) ),
								term( "then" ).next( actionSpec().endBy1( term( ";" ) ) ) )
				.map( new Map<Tuple3<String, List<FieldCheck[]>, List<ActionSpec>>, RuleSpec>() {
					@Override
					public RuleSpec map(Tuple3<String, List<FieldCheck[]>, List<ActionSpec>> arg0)
					{
						return new RuleSpec( arg0.a, //
								arg0.b.toArray( new FieldCheck[arg0.b.size()][] ), //
								arg0.c.toArray( new ActionSpec[arg0.c.size()] ) );
					}
				} );
	}


	/**
	 * get the rule parser.
	 * 
	 * @return the parser.
	 */
	private Parser<RuleSpec[]> rules()
	{
		return rule().many1().map( new Map<List<RuleSpec>, RuleSpec[]>() {
			@Override
			public RuleSpec[] map(List<RuleSpec> arg0)
			{
				return arg0.toArray( new RuleSpec[arg0.size()] );
			}
		} );
	}


	/**
	 * tests the parser.
	 * 
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args)
	{
		// String[] tests = { "a = \"adfadsf\"", "a like \"adfa\\\"dsf\"", "a.b like \"adfa\\\"dsf\"", };
		//
		// for( String t : tests )
		// System.out.println( instance.fieldCheck().from( instance.tokenizer(), instance.ignored() ).parse( t ) );
		//
		// System.out.println( "--" );
		// String event = "Event(a = \"adfadsf\", a like \"adfa\\\"dsf\" , a.b like \"adfa\\\"dsf\" )";
		// System.out
		// .println( Arrays.toString( instance.eventSpec().from( instance.tokenizer(), instance.ignored() ).parse( event ) ) );
		//
		// System.out.println( "--" );
		// String rule =
		// "rule \"mitsos\"\nwhen\nEvent(a = \"adfadsf\", a like \"adfa\\\"dsf\" , a.b like \"adfa\\\"dsf\" );\nEvent(a = \"adfadsf\", a like \"adfa\\\"dsf\" , a.b like \"adfa\\\"dsf\" );\nEvent(a = \"adfadsf\", a like \"adfa\\\"dsf\" , a.b like \"adfa\\\"dsf\" );\nthen PushAsIs(\"foo@bar:4040/\");\n";
		// System.out.println( instance.rule().from( instance.tokenizer(), instance.ignored() ).parse( rule ) );

		String rule = "rule \"mitsos\"\n" + //
				"when\n" + //
				"Event( Source.Host like \".*\\.cluster10\\.cloud\\.net\" );\n" + //
				"then\n" + //
				"PushAsIs(\"foo@bar:4040/\");\n";

		System.out.println( instance.rule().from( instance.tokenizer(), instance.ignored() ).parse( rule ) );
	}
}
