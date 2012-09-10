package gr.ntua.vision.monitoring.rules;

import org.json.simple.JSONObject;


/**
 * 
 */
public class CTOContainerObject {
    /***/
    public final String name;
    /***/
    public final String tenant;
    /***/
    private long        accessess;
    /***/
    private long        size;


    /**
     * Constructor.
     * 
     * @param tenant
     * @param name
     * @param size
     */
    public CTOContainerObject(final String tenant, final String name, final long size) {
        this.tenant = tenant;
        this.name = name;
        this.size = size;
        this.accessess = 1;
    }


    /**
     * @param objectSize
     */
    public void addObjectSize(final long objectSize) {
        size += objectSize;
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
        final CTOContainerObject other = (CTOContainerObject) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (tenant == null) {
            if (other.tenant != null)
                return false;
        } else if (!tenant.equals(other.tenant))
            return false;
        return true;
    }


    /**
     * @return
     */
    public long getAccessess() {
        return accessess;
    }


    /**
     * @return
     */
    public long getSize() {
        return size;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((tenant == null) ? 0 : tenant.hashCode());
        return result;
    }


    /**
     * 
     */
    public void incAccesses() {
        ++accessess;
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toJSONString();
    }


    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    private String toJSONString() {
        final JSONObject o = new JSONObject();

        o.put("name", name);
        o.put("sum-size", size);
        o.put("count-size", accessess);

        return o.toString();
    }
}
