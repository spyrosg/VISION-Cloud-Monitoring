package gr.ntua.vision.monitoring.rules;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is used to construct (load) rules from the class path. You can eithen specify the fully qualified java name of the class
 * of the rule to load, or just the plain name. If a class is not found, an attempt will made to load from inside a package.
 */
public class ClassPathRulesFactory implements RulesFactory {
    /***/
    private static final Logger    log                 = LoggerFactory.getLogger(ClassPathRulesFactory.class);
    /***/
    private final HashSet<String>  classesUnderPackage = new HashSet<String>();
    /***/
    private final VismoRulesEngine engine;
    /***/
    private final Package          pkg;


    /**
     * Constructor.
     * 
     * @param pkg
     * @param engine
     */
    public ClassPathRulesFactory(final Package pkg, final VismoRulesEngine engine) {
        this.pkg = pkg;
        this.engine = engine;
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RulesFactory#constructByName(java.lang.String)
     */
    @Override
    public VismoRule constructByName(final String ruleName) {
        final Class< ? > cls = tryLoadClass(ruleName);

        log.debug("matching class for {} => {}", ruleName, cls);

        final Constructor< ? > constructor = tryGetConstructor(cls, engine.getClass());

        return (VismoRule) invokeWithArguments(constructor, engine);
    }


    /**
     * @see gr.ntua.vision.monitoring.rules.RulesFactory#constructByNameWithArguments(java.lang.String, java.lang.Object[])
     */
    @Override
    public VismoRule constructByNameWithArguments(final String ruleName, final Object... args) {
        final Class< ? > cls = tryLoadClass(ruleName);

        log.debug("matching class for {} => {}", ruleName, cls);

        final Object[] newArgs = new Object[args.length + 1];

        newArgs[0] = engine;

        for (int i = 1; i < newArgs.length; ++i)
            newArgs[i] = args[i - 1];

        final Constructor< ? > constructor = tryGetConstructor(cls, toClasses(newArgs));

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
            try {
                return Class.forName(tryLoadFromPackage(pkg, className));
            } catch (final ClassNotFoundException e1) {
                log.error(className, e);

                throw new RuntimeException(e);
            }
        }
    }


    /**
     * @param pkg
     * @param className
     * @return
     */
    private String tryLoadFromPackage(final Package pkg, final String className) {
        if (classesUnderPackage.isEmpty())
            classesUnderPackage.addAll(getClassesForPackage(pkg));

        for (final String name : classesUnderPackage)
            if (name.contains(className))
                return name;

        return null;
    }


    /**
     * @param pkg
     * @return
     */
    private static ArrayList<String> getClassesForPackage(final Package pkg) {
        final ArrayList<String> classes = new ArrayList<String>();

        final String pkgname = pkg.getName();
        final String relPath = pkgname.replace('.', '/');
        final URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);

        if (resource == null)
            throw new RuntimeException("Unexpected problem: No resource for " + relPath);

        log.debug("Package: '" + pkgname + "' becomes Resource: '" + resource + "'");
        processDirectory(new File(resource.getPath()), pkgname, classes);

        return classes;
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
     * @param directory
     * @param pkgname
     * @param classes
     */
    private static void processDirectory(final File directory, final String pkgname, final ArrayList<String> classes) {
        log.debug("Reading Directory '" + directory + "'");

        final String[] files = directory.list();

        for (final String fileName : files) {
            if (!fileName.endsWith(".class"))
                continue;

            final String className = pkgname + '.' + fileName.substring(0, fileName.length() - 6);

            classes.add(className);

            final File subdir = new File(directory, fileName);

            if (subdir.isDirectory())
                processDirectory(subdir, pkgname + '.' + fileName, classes);
        }
    }


    /**
     * @param args
     * @return
     */
    private static Class< ? >[] toClasses(final Object... args) {
        final Class< ? >[] arr = new Class< ? >[args.length];

        for (int i = 0; i < args.length; ++i)
            arr[i] = args[i].getClass();

        return arr;
    }


    /**
     * @param cls
     * @param classArgs
     * @return
     * @throws RuntimeException
     *             on some error.
     */
    private static Constructor< ? > tryGetConstructor(final Class< ? > cls, final Class< ? >... classArgs) {
        for (int i = 0; i < classArgs.length; ++i)
            log.debug("classArgs[{}].class = {}", i, classArgs[i]);

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
