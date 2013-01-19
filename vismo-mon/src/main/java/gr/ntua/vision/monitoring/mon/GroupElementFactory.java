package gr.ntua.vision.monitoring.mon;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * This is used to abstract away the construction of {@link GroupElement}s.
 */
public class GroupElementFactory {
    /**
     * Parse the string and construct a {@link GroupElement} object.
     * 
     * @param s
     *            the string.
     * @return a {@link GroupElement} object.
     * @throws UnknownHostException
     */
    @SuppressWarnings("static-method")
    public GroupElement buildFromString(final String s) throws UnknownHostException {
        final String[] fields = s.split(":");

        return new GroupElement(fields[0], InetAddress.getByName(fields[1]));
    }
}
