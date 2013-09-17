package gr.ntua.vision.monitoring.resources;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * This bean is used to serialize rules to json. There's also the {@link RuleBean} hierarchy for deserializing rules from json.
 * NOTE: we don't use the same objects for ser/deser 'cause we're forced to accept different rule representations.
 */
@XmlRootElement
public class RuleIdBean {
    /***/
    @XmlAttribute(name = "class")
    private String clz;
    /***/
    private String id;


    /**
     * Constructor.
     */
    public RuleIdBean() {
    }


    /**
     * Constructor.
     * 
     * @param id
     * @param clz
     */
    public RuleIdBean(final String id, final String clz) {
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
