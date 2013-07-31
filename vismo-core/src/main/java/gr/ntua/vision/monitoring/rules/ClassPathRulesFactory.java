package gr.ntua.vision.monitoring.rules;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to construct (load) rules from the class path. You can eithen specify the fully qualified java name of the class
 * of the rule to load, or just the plain name. If a class is not found, an attempt will made to load from inside a package.
 */
public class ClassPathRulesFactory extends AbstractRulesFactory {
    /***/
    private static final Logger         log = LoggerFactory.getLogger(ClassPathRulesFactory.class);
    /***/
    private final Package               pkg;
    /***/
    private final ClassPathResourceList resourceList;


    /**
     * Constructor.
     * 
     * @param next
     * @param engine
     * @param pkg
     */
    public ClassPathRulesFactory(final RulesFactory next, final VismoRulesEngine engine, final Package pkg) {
        super(next, engine);
        this.pkg = pkg;
        this.resourceList = new ClassPathResourceList();
    }


    /**
     * Constructor.
     * 
     * @param engine
     * @param pkg
     */
    public ClassPathRulesFactory(final VismoRulesEngine engine, final Package pkg) {
        this(null, engine, pkg);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RulesFactory#buildFrom(gr.ntua.vision.monitoring.rules.RuleBean)
     */
    @Override
    public VismoRule buildFrom(final RuleBean bean) {
        if (!(bean instanceof DefaultRuleBean))
            return next().buildFrom(bean);

        log.debug("applicable bean {}", bean);

        final DefaultRuleBean b = (DefaultRuleBean) bean;

        if (b.isPeriodic())
            return constructByName(b.getName());

        return constructByNameWithArguments(b.getName(), b.getPeriod());
    }


    /**
     * @param ruleName
     * @return a {@link VismoRule}, loaded by name.
     */
    private VismoRule constructByName(final String ruleName) {
        final Class< ? > cls = tryLoadClass(ruleName);

        log.debug("matching class for {} => {}", ruleName, cls);

        final Constructor< ? > constructor = tryGetConstructor(cls, engine.getClass());

        log.debug("matching constructor: {}", constructor);

        return (VismoRule) invokeWithArguments(constructor, engine);
    }


    /**
     * @param ruleName
     * @param args
     * @return a {@link VismoRule} loaded by name and arguments.
     */
    private VismoRule constructByNameWithArguments(final String ruleName, final Object... args) {
        final Class< ? > cls = tryLoadClass(ruleName);

        log.debug("matching class for {} => {}", ruleName, cls);

        final Object[] newArgs = new Object[args.length + 1];

        newArgs[0] = engine;
        System.arraycopy(args, 0, newArgs, 1, args.length);

        final Constructor< ? > constructor = tryGetConstructor(cls, toClasses(newArgs));

        log.debug("matching constructor: {}", constructor);

        return (VismoRule) invokeWithArguments(constructor, newArgs);
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
    private Class< ? > tryLoadClass(final String className) {
        try {
            return Class.forName(className);
        } catch (final ClassNotFoundException e) {
            final String fcqn = tryLoadFromPackage(pkg, className);

            if (fcqn == null)
                throw new IllegalArgumentException("no matching class found for class-name: " + className);

            try {
                return Class.forName(fcqn);
            } catch (final ClassNotFoundException e1) {
                log.error(className, e);

                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Try to find the given class by name in the specified package.
     * 
     * @param pckg
     * @param className
     * @return the fully qualified java name the corresponds to the given <code>className</code>, <code>null</code> if there's no
     *         match.
     */
    private String tryLoadFromPackage(final Package pckg, final String className) {
        final String pkgName = pckg.getName().replace(".", "/");
        final String classSuffix = className + ".class";

        log.trace("pkg-name: '{}', class-name: '{}'", pkgName, className);

        for (final String res : resourceList.getResources())
            if (res.contains(pkgName) && res.endsWith(classSuffix)) {
                log.trace("resource matching class: {}", res);

                final String fqcn = translateResourceToFQCN(pckg.getName(), res);

                log.trace("fully qualified name: {}", fqcn);

                return fqcn;
            }

        log.warn("no matching class found for class-name: {}", className);

        return null;
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
     * Get the classes of given array of objects.
     * 
     * @param args
     * @return the array of classes corresponding to the arguments.
     */
    private static Class< ? >[] toClasses(final Object... args) {
        final Class< ? >[] arr = new Class< ? >[args.length];

        // FIXME: handle primitive types; Long.class != long.class
        for (int i = 0; i < args.length; ++i)
            if (args[i].getClass() == Long.class)
                arr[i] = long.class;
            else
                arr[i] = args[i] != null ? args[i].getClass() : null;

        return arr;
    }


    /**
     * Given the name of a resource in a package, return the fully qualified class name that stands for given resource.
     * 
     * @param pkgName
     * @param res
     * @return the fully qualified class name of the <code>resource</code>.
     */
    private static String translateResourceToFQCN(final String pkgName, final String res) {
        return res.replace(".class", "").replace("/", ".").replace(pkgName, "@").replaceAll("^[^@]*@", pkgName);
    }


    /**
     * @param cls
     * @param classArgs
     * @return the appropriate constructor for given argument of classes.
     * @throws RuntimeException
     *             on some error.
     */
    private static Constructor< ? > tryGetConstructor(final Class< ? > cls, final Class< ? >... classArgs) {
        log.trace("constructor for arguments: {}", Arrays.toString(classArgs));

        try {
            return cls.getConstructor(classArgs);
        } catch (final NoSuchMethodException e) {
            log.error(cls.getCanonicalName(), e);

            throw new RuntimeException(e);
        } catch (final SecurityException e) {
            log.error(cls.getCanonicalName(), e);

            throw new RuntimeException(e);
        }
    }
}
