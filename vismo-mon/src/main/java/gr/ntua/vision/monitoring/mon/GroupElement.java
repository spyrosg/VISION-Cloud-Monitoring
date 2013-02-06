package gr.ntua.vision.monitoring.mon;

import java.net.InetAddress;
import java.util.Date;


/**
 * A {@link GroupElement} may be member of a {@link GroupMembership}. Elements are value objects that can be uniquely identified
 * by their inet address.
 */
public class GroupElement {
    /** the element's address. */
    public final InetAddress addr;
    /** when was this element last updated (in milliseconds since the epoch)? */
    public final long        lastUpdated;
    /***/
    public final String      s;


    /**
     * Constructor.
     * 
     * @param addr
     *            the element's address.
     * @param s
     */
    public GroupElement(final InetAddress addr, final String s) {
        this(addr, s, System.currentTimeMillis());
    }


    /**
     * Constructor.
     * 
     * @param addr
     *            the address.
     * @param s
     * @param lastUpdated
     *            when was this element last updated? (in milliseconds since the epoch)
     */
    public GroupElement(final InetAddress addr, final String s, final long lastUpdated) {
        this.addr = addr;
        this.s = s;
        this.lastUpdated = lastUpdated;
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final GroupElement other = (GroupElement) obj;
        if (addr == null) {
            if (other.addr != null)
                return false;
        } else if (!addr.equals(other.addr))
            return false;
        if (s == null) {
            if (other.s != null)
                return false;
        } else if (!s.equals(other.s))
            return false;
        return true;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((addr == null) ? 0 : addr.hashCode());
        result = prime * result + ((s == null) ? 0 : s.hashCode());
        return result;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<GroupElement(" + s + ") @ " + addr.getHostAddress() + " (updated " + toDate(lastUpdated) + ")>";
    }


    /**
     * Return a human readable representation of the time-stamp.
     * 
     * @param ts
     *            the time-stamp.
     * @return a human readable representation of the time-stamp.
     */
    private static Date toDate(final long ts) {
        return new Date(ts);
    }
}
