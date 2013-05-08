package gr.ntua.vision.monitoring.rules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;


/**
 * Return a list of list of all resources available in the classpath. Source code from
 * {@link "http://stackoverflow.com/a/3923182"}.
 */
class ClassPathResourceList {
    /***/
    private static final String     DEFAULT_RESOURCE_PATTERN = ".*";
    /***/
    private final ArrayList<String> list;
    /***/
    private final Pattern           patt;


    /**
     * Constructor.
     */
    public ClassPathResourceList() {
        this(DEFAULT_RESOURCE_PATTERN);
    }


    /**
     * Constructor.
     * 
     * @param patt
     */
    public ClassPathResourceList(final String patt) {
        this.patt = Pattern.compile(patt);
        this.list = new ArrayList<String>();
    }


    /**
     * @return the list of resource.
     */
    public List<String> getResources() {
        if (list.size() != 0)
            return list;

        final ArrayList<String> tmp = getResources(patt);

        list.addAll(tmp);

        return list;
    }


    /**
     * For all elements of java.class.path get a list of resources that match given pattern.
     * 
     * @param pattern
     *            the pattern to match
     * @return the resources in the order they are found.
     */
    public static ArrayList<String> getResources(final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final String classPath = System.getProperty("java.class.path", ".");
        final String[] classPathElements = classPath.split(":");

        for (final String element : classPathElements)
            retval.addAll(getResources(element, pattern));

        return retval;
    }


    /**
     * @param element
     * @param pattern
     * @return the resources in the order they are found.
     */
    private static ArrayList<String> getResources(final String element, final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final File file = new File(element);

        if (file.isDirectory())
            retval.addAll(getResourcesFromDirectory(file, pattern));
        else
            retval.addAll(getResourcesFromJarFile(file, pattern));

        return retval;
    }


    /**
     * @param directory
     * @param pattern
     * @return the list of resource found in the directory.
     */
    private static ArrayList<String> getResourcesFromDirectory(final File directory, final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();

        for (final File file : directory.listFiles())
            if (file.isDirectory())
                retval.addAll(getResourcesFromDirectory(file, pattern));
            else
                try {
                    final String fileName = file.getCanonicalPath();

                    if (pattern.matcher(fileName).matches())
                        retval.add(fileName);
                } catch (final IOException e) {
                    throw new Error(e);
                }

        return retval;
    }


    /**
     * @param file
     * @param pattern
     * @return the list of resource found in the jar.
     */
    private static ArrayList<String> getResourcesFromJarFile(final File file, final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final ZipFile zf;

        try {
            zf = new ZipFile(file);
        } catch (final ZipException e) {
            throw new Error(e);
        } catch (final IOException e) {
            throw new Error(e);
        }

        final Enumeration< ? extends ZipEntry> e = zf.entries();

        while (e.hasMoreElements()) {
            final ZipEntry ze = e.nextElement();
            final String fileName = ze.getName();
            final boolean accept = pattern.matcher(fileName).matches();
            if (accept)
                retval.add(fileName);
        }

        try {
            zf.close();
        } catch (final IOException e1) {
            throw new Error(e1);
        }

        return retval;
    }
}
