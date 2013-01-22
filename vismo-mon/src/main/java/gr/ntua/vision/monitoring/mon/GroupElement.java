package gr.ntua.vision.monitoring.mon;

import java.net.InetAddress;
import java.util.Date;


/**
 * A {@link GroupElement} may be member of a {@link GroupMembership}. Each element can be distinguished by its inet address, its
 * identifier and its time-stamp.
 */
public class GroupElement {
    /** the address. */
    private final InetAddress addr;
    /** the id. */
    private final String      id;
    /** when was this element last updated (in milliseconds since the epoch)? */
    private final long        lastUpdated;


    /**
     * Constructor.
     * 
     * @param id
     *            the id.
     * @param addr
     *            the address.
     */
    public GroupElement(final String id, final InetAddress addr) {
        this(id, addr, System.currentTimeMillis());
    }


    /**
     * Constructor.
     * 
     * @param id
     *            the id.
     * @param addr
     *            the address.
     * @param lastUpdated
     *            when was this element last updated (in milliseconds since the epoch)?
     */
    public GroupElement(final String id, final InetAddress addr, final long lastUpdated) {
        this.id = id;
        this.addr = addr;
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
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (lastUpdated != other.lastUpdated)
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
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (int) (lastUpdated ^ (lastUpdated >>> 32));
        return result;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<GroupElement: " + id + " @ " + addr.getHostAddress() + " (updated " + toDate(lastUpdated) + ")>";
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
