package gr.ntua.vision.monitoring.resources;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 
 */
@XmlRootElement
public class MemoryUsageBean {
    /***/
    @XmlElement(name = "free-memory")
    private long freeMemoryInBytes;
    /***/
    @XmlElement(name = "total-memory")
    private long totalMemoryInBytes;


    /**
     * Constructor.
     */
    private MemoryUsageBean() {
    }


    /**
     * @return the freeMemoryInBytes
     */
    public long getFreeMemoryInBytes() {
        return freeMemoryInBytes;
    }


    /**
     * @return the totalMemoryInBytes
     */
    public long getTotalMemoryInBytes() {
        return totalMemoryInBytes;
    }


    /**
     * @param freeMemoryInBytes
     *            the freeMemoryInBytes to set
     */
    public void setFreeMemoryInBytes(final long freeMemoryInBytes) {
        this.freeMemoryInBytes = freeMemoryInBytes;
    }


    /**
     * @param totalMemoryInBytes
     *            the totalMemoryInBytes to set
     */
    public void setTotalMemoryInBytes(final long totalMemoryInBytes) {
        this.totalMemoryInBytes = totalMemoryInBytes;
    }


    /**
     * @return the percent of used memory in the jvm.
     */
    public static MemoryUsageBean collect() {
        final MemoryUsageBean u = new MemoryUsageBean();

        u.setFreeMemoryInBytes(Runtime.getRuntime().freeMemory());
        u.setTotalMemoryInBytes(Runtime.getRuntime().totalMemory());

        return u;
    }
}
