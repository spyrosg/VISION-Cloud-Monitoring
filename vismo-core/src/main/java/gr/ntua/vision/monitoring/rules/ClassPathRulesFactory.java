package gr.ntua.vision.monitoring.rules;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to construct (load) rules from the class path.
 */
public class ClassPathRulesFactory implements RulesFactory {
    /***/
    private static final Logger    log = LoggerFactory.getLogger(ClassPathRulesFactory.class);
    /***/
    private final VismoRulesEngine engine;


    /**
     * Constructor.
     * 
     * @param engine
     */
    public ClassPathRulesFactory(final VismoRulesEngine engine) {
        this.engine = engine;
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RulesFactory#buildByName(java.lang.String)
     */
    @Override
    public VismoRule buildByName(final String ruleName) {
        final Class< ? > cls = tryLoadClass(ruleName);

        log.debug("matching class for {} => {}", ruleName, cls);

        final Constructor< ? > constructor = tryGetConstructor(cls);

        return (VismoRule) invokeWithArguments(constructor, engine);

    }


    /**
     * @param cls
     * @return
     * @throws RuntimeException
     *             on some error.
     */
    private Constructor< ? > tryGetConstructor(final Class< ? > cls) {
        try {
            return cls.getConstructor(engine.getClass());
        } catch (final NoSuchMethodException e) {
            log.error(cls.getCanonicalName(), e);

            throw new RuntimeException(e);
        } catch (final SecurityException e) {
            log.error(cls.getCanonicalName(), e);

            throw new RuntimeException(e);
        }
    }


    /**
     * Attempt to call the constructor with given argument.
     * 
     * @param constructor
     *            the constructor to invoke.
     * @param args
     *            the list of arguments to the constructor.
     * @return on success, a new instance corresponding to given constructor and arguments.
     * @throws RuntimeException
     *             on some error.
     */
    private static Object invokeWithArguments(final Constructor< ? > constructor, final Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (final InstantiationException e) {
            log.error(constructor.getName(), e);

            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            log.error(constructor.getName(), e);

            throw new RuntimeException(e);
        } catch (final IllegalArgumentException e) {
            log.error(constructor.getName(), e);

            throw new RuntimeException(e);
        } catch (final InvocationTargetException e) {
            log.error(constructor.getName(), e);

            throw new RuntimeException(e);
        }
    }


    /**
     * Load the class that is represented by given name.
     * 
     * @param className
     *            the class name.
     * @return the {@link Class} instance for given class name.
     * @throws RuntimeException
     *             on some error.
     */
    private static Class< ? > tryLoadClass(final String className) {
        try {
            return Class.forName(className);
        } catch (final ClassNotFoundException e) {
            log.error(className, e);

            throw new RuntimeException(e);
        }
    }
}
