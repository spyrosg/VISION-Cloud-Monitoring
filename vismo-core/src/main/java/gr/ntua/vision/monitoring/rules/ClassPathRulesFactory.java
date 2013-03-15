package gr.ntua.vision.monitoring.rules;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

        log.debug("matching constructor: {}", constructor);

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
            try {
                return Class.forName(tryLoadFromPackage(pkg, className));
            } catch (final ClassNotFoundException e1) {
                log.error(className, e);

                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Try to find the given class by name in the specified package.
     * 
     * @param pkg
     * @param className
     * @return the fully qualified java name the corresponds to the given <code>className</code>, <code>null</code> if there's no
     *         match.
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
     * @return the list of class names found in the package.
     */
    private static ArrayList<String> getClassesForPackage(final Package pkg) {
        final ArrayList<String> classes = new ArrayList<String>();

        final String pkgname = pkg.getName();
        final String relPath = pkgname.replace('.', '/');
        final URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);

        if (resource == null)
            throw new RuntimeException("Unexpected problem: No resource for " + relPath);

        log.trace("package '{}' => {}", pkgname, resource);
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
        log.trace("for directory ' {}'");

        final String[] files = directory.list();

        for (final String fileName : files) {
            if (!fileName.endsWith(".class"))
                continue;

            final String className = pkgname + '.' + fileName.substring(0, fileName.length() - 6);

            classes.add(className);

            final File subdir = new File(directory, fileName);

            if (subdir.isDirectory())
                processDirectory(subdir, pkgname + "." + fileName, classes);
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

        for (int i = 0; i < args.length; ++i)
            arr[i] = args[i] != null ? args[i].getClass() : null;

        return arr;
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