package gr.ntua.vision.monitoring.rules;

/**
 * 
 */
public class DefaultRuleBean implements RuleBean {
    /***/
    private String name;
    /***/
    private long   period;


    /**
     * Constructor.
     */
    public DefaultRuleBean() {
    }


    /**
     * Constructor.
     * 
     * @param name
     * @param period
     */
    public DefaultRuleBean(final String name, final long period) {
        this.name = name;
        this.period = period;
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * @return the period
     */
    public long getPeriod() {
        return period;
    }


    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }


    /**
     * @param period
     *            the period to set
     */
    public void setPeriod(final long period) {
        this.period = period;
    }
}
