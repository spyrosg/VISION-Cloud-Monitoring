package gr.ntua.vision.monitoring.resources;

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
     */
    public DefaultRuleBean(final String name) {
        this.name = name;
        this.period = -1;
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
     * @return <code>true</code> if the bean has a positive integer period.
     */
    public boolean isPeriodic() {
        return period <= 0;
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
