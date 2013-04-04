package gr.ntua.vision.monitoring.rules;

/**
 * 
 */
public abstract class AbstractRulesFactory implements RulesFactory {
    /***/
    protected final VismoRulesEngine engine;
    /***/
    private final RulesFactory       next;


    /**
     * Constructor.
     * 
     * @param next
     * @param engine
     */
    public AbstractRulesFactory(final RulesFactory next, final VismoRulesEngine engine) {
        this.next = next;
        this.engine = engine;
    }


    /**
     * Constructor.
     * 
     * @param engine
     */
    public AbstractRulesFactory(final VismoRulesEngine engine) {
        this(null, engine);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RulesFactory#next()
     */
    @Override
    public RulesFactory next() {
        return next;
    }
}
