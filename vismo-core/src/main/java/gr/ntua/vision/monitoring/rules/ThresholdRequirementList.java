package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;
import gr.ntua.vision.monitoring.resources.ThresholdRequirementBean;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 */
public class ThresholdRequirementList {
    /***/
    private final List<ThresholdRequirement> list;


    /**
     * Constructor.
     * 
     * @param list
     */
    private ThresholdRequirementList(final List<ThresholdRequirement> list) {
        this.list = list;
    }


    /**
     * @param events
     * @param filterUnits
     * @return the possbily empty list of violations.
     */
    public ViolationsList haveViolations(final List<MonitoringEvent> events, final List<String> filterUnits) {
        final ViolationsList violList = new ViolationsList();

        for (final ThresholdRequirement req : list) {
            final Violation v = req.isViolated(events, filterUnits);

            if (v != null)
                violList.add(v);
        }

        return violList;
    }


    /**
     * @param event
     * @param filterUnits
     * @return the possibly empty list of violations.
     */
    public ViolationsList haveViolations(final MonitoringEvent event, final List<String> filterUnits) {
        final ViolationsList violList = new ViolationsList();

        for (final ThresholdRequirement req : list) {
            final Violation v = req.isViolated(event, filterUnits);

            if (v != null)
                violList.add(v);
        }

        return violList;
    }


    /**
     * @param e
     * @return <code>true</code> if the event matches any of the requirements.
     */
    public boolean isApplicable(final MonitoringEvent e) {
        for (final ThresholdRequirement req : list)
            if (req.isApplicable(e))
                return true;

        return false;
    }


    /**
     * @return the list size.
     */
    public int size() {
        return list.size();
    }


    /**
     * @param beanList
     * @return a {@link ThresholdRequirementList}.
     */
    public static ThresholdRequirementList from(final List<ThresholdRequirementBean> beanList) {
        final ArrayList<ThresholdRequirement> list = new ArrayList<ThresholdRequirement>();

        for (final ThresholdRequirementBean bean : beanList)
            list.add(ThresholdRequirement.from(bean));

        return new ThresholdRequirementList(list);
    }
}
