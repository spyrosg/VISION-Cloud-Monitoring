package gr.ntua.monitoring.mon;

import java.net.InetAddress;
import java.util.Date;


/**
 * 
 */
public class GroupElement {
    /***/
    private final InetAddress addr;
    /***/
    private final String      id;
    /***/
    private final long        lastUpdated;


    /**
     * Constructor.
     * 
     * @param id
     * @param addr
     */
    public GroupElement(final String id, final InetAddress addr) {
        this(id, addr, System.currentTimeMillis());
    }


    /**
     * Constructor.
     * 
     * @param id
     * @param addr
     * @param lastUpdated
     */
    public GroupElement(final String id, final InetAddress addr, final long lastUpdated) {
        this.id = id;
        this.addr = addr;
        this.lastUpdated = lastUpdated;
    }


    /**
     * Check whether <code>this</code> belongs to the group.
     * 
     * @param mship
     *            the group to check membership.
     * @return <code>true</code> iff <code>this</code> is a member of the group, <code>false</code> otherwise.
     */
    public boolean belongsTo(final GroupMembership mship) {
        return mship.contains(this);
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
        if (!(obj instanceof GroupElement))
            return false;
        final GroupElement other = (GroupElement) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
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
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "#<GroupElement: " + id + " @ " + addr + " (updated = " + toDate(lastUpdated) + ")>";
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
