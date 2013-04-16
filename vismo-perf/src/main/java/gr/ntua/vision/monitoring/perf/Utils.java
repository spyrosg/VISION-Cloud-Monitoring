package gr.ntua.vision.monitoring.perf;

import java.io.File;


/**
 * 
 */
public class Utils {
    /**
     * Constructor.
     */
    private Utils() {
    }


    /**
     * @param path
     * @throws Error
     */
    public static void requireFile(final String path) throws Error {
        if (!new File(path).exists())
            throw new Error("no such file or directory: " + path);
    }
}
