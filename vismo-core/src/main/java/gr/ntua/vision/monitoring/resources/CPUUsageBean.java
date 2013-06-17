package gr.ntua.vision.monitoring.resources;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 
 */
@XmlRootElement
public class CPUUsageBean {
    /***/
    private static final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
    /***/
    @XmlElement(name = "avg-cpu-load")
    private double                             hostCPULoad;


    /**
     * Constructor.
     */
    public CPUUsageBean() {
    }


    /**
     * @return the hostCPULoad
     */
    public double getHostCPULoad() {
        return hostCPULoad;
    }


    /**
     * @param hostCPULoad
     *            the hostCPULoad to set
     */
    public void setHostCPULoad(final double hostCPULoad) {
        this.hostCPULoad = hostCPULoad;
    }


    /**
     * @return
     */
    public static CPUUsageBean get() {
        final CPUUsageBean bean = new CPUUsageBean();

        bean.setHostCPULoad(osBean.getSystemLoadAverage());

        return bean;
    }
}
