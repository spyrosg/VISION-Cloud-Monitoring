package endtoend;

/**
 *
 */
public class FakeEventConsumer {
    /***/
    private final EventRegister registry;


    /**
     * Constructor.
     * 
     * @param registry
     */
    public FakeEventConsumer(final EventRegister registry) {
        this.registry = registry;
    }


    public void start() {
        registry.register("foo", null);
    }
}
