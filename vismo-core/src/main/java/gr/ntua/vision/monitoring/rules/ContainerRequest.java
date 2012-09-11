package gr.ntua.vision.monitoring.rules;

/**
 * A container request represents a user action (read, write, delete) on the contents (objects) of a container. A container
 * belongs to a specific tenant. A request is initiated by a user.
 */
class ContainerRequest {
    /***/
    final String container;
    /***/
    final String tenant;
    /***/
    final String user;


    /**
     * Constructor.
     * 
     * @param tenant
     * @param container
     * @param user
     */
    public ContainerRequest(final String tenant, final String container, final String user) {
        this.tenant = tenant;
        this.container = container;
        this.user = user;
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((container == null) ? 0 : container.hashCode());
        result = prime * result + ((tenant == null) ? 0 : tenant.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }


    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ContainerRequest other = (ContainerRequest) obj;
        if (container == null) {
            if (other.container != null)
                return false;
        } else if (!container.equals(other.container))
            return false;
        if (tenant == null) {
            if (other.tenant != null)
                return false;
        } else if (!tenant.equals(other.tenant))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }
}
