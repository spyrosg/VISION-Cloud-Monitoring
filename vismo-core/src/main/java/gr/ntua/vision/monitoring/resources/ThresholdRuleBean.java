package gr.ntua.vision.monitoring.resources;

import gr.ntua.vision.monitoring.rules.ThresholdPeriodicRule;
import gr.ntua.vision.monitoring.rules.ThresholdRule;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * This is just a data holder, the exchange media format for {@link ThresholdRule}s or {@link ThresholdPeriodicRule}s.
 */
@XmlRootElement
public class ThresholdRuleBean implements RuleBean {
    /***/
    private String                         filterUnit;
    /***/
    private String                         id;
    /***/
    private String                         operation;
    /***/
    private long                           period       = -1;
    /***/
    private List<ThresholdRequirementBean> requirements = new ArrayList<ThresholdRequirementBean>();
    /***/
    private String                         topic;


    /**
     * Default constructor.
     */
    public ThresholdRuleBean() {
    }


    /**
     * @param metric
     * @param predicate
     * @param threshold
     * @return <code>this</code>.
     */
    public ThresholdRuleBean addRequirement(final String metric, final String predicate, final double threshold) {
        final List<ThresholdRequirementBean> list = getRequirements() != null ? getRequirements()
                                                                             : new ArrayList<ThresholdRequirementBean>();
        final ThresholdRequirementBean req = new ThresholdRequirementBean();

        req.setMetric(metric);
        req.setPredicate(predicate);
        req.setThreshold(threshold);
        list.add(req);

        return this;
    }


    /**
     * @param metric
     * @param aggregationMethod
     * @param predicate
     * @param threshold
     * @return <code>this</code>.
     */
    public ThresholdRuleBean addRequirement(final String metric, final String aggregationMethod, final String predicate,
            final double threshold) {
        final List<ThresholdRequirementBean> list = getRequirements() != null ? getRequirements()
                                                                             : new ArrayList<ThresholdRequirementBean>();
        final ThresholdRequirementBean req = new ThresholdRequirementBean();

        req.setMetric(metric);
        req.setAggregationMethod(aggregationMethod);
        req.setPredicate(predicate);
        req.setThreshold(threshold);
        list.add(req);

        return this;
    }


    /**
     * @return the filterUnit
     */
    public String getFilterUnit() {
        return filterUnit;
    }


    /**
     * @return the id
     */
    public String getId() {
        return id;
    }


    /**
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }


    /**
     * @return the period
     */
    public long getPeriod() {
        return period;
    }


    /**
     * @return the requirements
     */
    public List<ThresholdRequirementBean> getRequirements() {
        return requirements;
    }


    /**
     * @return the topic
     */
    public String getTopic() {
        return topic;
    }


    /**
     * @param filterUnit
     *            the filterUnit to set
     */
    public void setFilterUnit(final String filterUnit) {
        this.filterUnit = filterUnit;
    }


    /**
     * @param id
     *            the id to set
     */
    public void setId(final String id) {
        this.id = id;
    }


    /**
     * @param operation
     *            the operation to set
     */
    public void setOperation(final String operation) {
        this.operation = operation;
    }


    /**
     * @param period
     *            the period to set
     */
    public void setPeriod(final long period) {
        this.period = period;
    }


    /**
     * @param requirements
     *            the requirements to set
     */
    public void setRequirements(final List<ThresholdRequirementBean> requirements) {
        this.requirements = requirements;
    }


    /**
     * @param topic
     *            the topic to set
     */
    public void setTopic(final String topic) {
        this.topic = topic;
    }
}
