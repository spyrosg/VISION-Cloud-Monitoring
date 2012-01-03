package gr.ntua.vision.monitoring.rules.parser;

import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;


/**
 * The rule specification.
 */
public class RuleSpec {
    /** actions to perform. */
    public final ActionSpec[]   actions;
    /** event field checks. */
    public final FieldCheck[][] checks;
    /** rule ID. */
    public final UUID           id;
    /** rule name. */
    public final String         name;


    /**
     * c/tor.
     * 
     * @param name
     * @param checks
     * @param actions
     */
    RuleSpec(final String name, final FieldCheck[][] checks, final ActionSpec[] actions) {
        this.name = name;
        this.id = UUID.randomUUID();
        this.checks = checks;
        this.actions = actions;
    }


    /**
     * normalize the event matching expression. The first dimension binds its contents with a logical AND and the second dimension
     * with a logical OR. Predicates are constructed by this method to directly operate on {@link Event}s.
     * 
     * @return the normal form of the expression.
     */
    public Predicate<Event>[][] normalizeChecks() {
        final List<Pair<FieldCheck, List<FieldCheck>>> checksByField = Lists.newArrayList();

        for( final FieldCheck[] r : checks )
            for( final FieldCheck c : r ) {
                Pair<FieldCheck, List<FieldCheck>> pair = null;

                for( final Pair<FieldCheck, List<FieldCheck>> p : checksByField )
                    if( p.a.sameFieldWith( c ) ) {
                        pair = p;
                        break;
                    }

                if( pair == null ) //
                    checksByField.add( pair = new Pair<FieldCheck, List<FieldCheck>>( c, Lists.<FieldCheck> newArrayList() ) );

                pair.b.add( c );
            }

        @SuppressWarnings("unchecked")
        final Predicate<Event>[][] xpr = new Predicate[checksByField.size()][];

        for( int i = 0; i < xpr.length; ++i ) {
            final List<FieldCheck> chks = checksByField.get( i ).b;
            @SuppressWarnings("unchecked")
            final Predicate<Event>[] predicates = new Predicate[chks.size()];

            for( int j = 0; j < predicates.length; ++j )
                predicates[j] = chks.get( j ).toPredicate();

            xpr[i] = predicates;
        }

        return xpr;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append( "RuleSpec [name=" );
        builder.append( name );
        builder.append( ", id=" );
        builder.append( id );
        builder.append( ", checks=" );
        builder.append( Arrays.toString( checks ) );
        builder.append( ", actions=" );
        builder.append( Arrays.toString( actions ) );
        builder.append( "]" );
        return builder.toString();
    }
}
