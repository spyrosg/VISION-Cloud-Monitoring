package gr.ntua.vision.monitoring.rules.parser;

import gr.ntua.vision.monitoring.model.Event;
import gr.ntua.vision.monitoring.rules.ActionHandler;
import gr.ntua.vision.monitoring.rules.Actions;

import java.util.Arrays;

import com.google.common.base.Function;


/**
 * The action specification.
 */
public class ActionSpec {
    /** the action. */
    public final Actions  action;
    /** the action arguments. */
    public final Object[] arguments;


    /**
     * @param action
     * @param arguments
     */
    ActionSpec(final Actions action, final Object[] arguments) {
        this.action = action;
        this.arguments = arguments;
    }


    /**
     * create the action handler.
     * 
     * @param handler
     *            the action handler.
     * @return the functor.
     */
    public Function<Event, Boolean> actionFunctor(final ActionHandler handler) {
        return new Function<Event, Boolean>() {
            @Override
            public Boolean apply(final Event event) {
                switch( action ) {
                    case PushAggregated:
                        return handler.transmit( event, (String) arguments[0] );

                    case Store:
                        return handler.store( event, (String) arguments[0] );

                    default:
                }
                return false;
            }
        };
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append( "ActionSpec [action=" );
        builder.append( action );
        builder.append( ", arguments=" );
        builder.append( Arrays.toString( arguments ) );
        builder.append( "]" );
        return builder.toString();
    }
}
