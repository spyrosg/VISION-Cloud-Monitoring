package gr.ntua.vision.monitoring.resources;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 
 */
@XmlRootElement
public class RuleBean {
    /***/
    @XmlAttribute(name = "class")
    private String clz;
    /***/
    private String id;


    /**
     * Constructor.
     */
    public RuleBean() {
    }


    /**
     * Constructor.
     * 
     * @param id
     * @param clz
     */
    public RuleBean(final String id, final String clz) {
        this.id = id;
        this.clz = clz;
    }


    /**
     * @return the clz
     */
    public String getClz() {
        return clz;
    }


    /**
     * @return the id
     */
    public String getId() {
        return id;
    }


    /**
     * @param clz
     *            the clz to set
     */
    public void setClz(final String clz) {
        this.clz = clz;
    }


    /**
     * @param id
     *            the id to set
     */
    public void setId(final String id) {
        this.id = id;
    }
}
