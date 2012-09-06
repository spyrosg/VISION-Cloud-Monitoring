package gr.ntua.vision.monitoring.rules;

public class Container {
	public final String tenant;
	public final String name;
	private long size;
	private long accessess;

	public Container(String tenant, String name, long size) {
		this.tenant = tenant;
		this.name = name;
		this.size = size;
		this.accessess = 1;
	}

	public void addObjectSize(final long objectSize) {
		size += objectSize;
	}

	public void incAccesses() {
		++accessess;
	}

	public long getSize() {
		return size;
	}

	public long getAccessess() {
		return accessess;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((tenant == null) ? 0 : tenant.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Container other = (Container) obj;
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

	@Override
	public String toString() {
		return "#<Container [tenant=" + tenant + ", name=" + name + ", size=" + size + ", accessess=" + accessess + ">";
	}
}
