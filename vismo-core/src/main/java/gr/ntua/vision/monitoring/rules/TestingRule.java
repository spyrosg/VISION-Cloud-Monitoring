package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.events.MonitoringEvent;


/**
 * @author tmessini
 */
public class TestingRule extends Rule {
    /***/
    private final String desc;
    /***/
    private final String name;


    /**
     * @param engine
     * @param name
     * @param desc
     */
    public TestingRule(final VismoRulesEngine engine, final String name, final String desc) {
        super(engine);
        this.name = name;
        this.desc = desc;
    }


    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TestingRule other = (TestingRule) obj;
        if (desc == null) {
            if (other.desc != null)
                return false;
        } else if (!desc.equals(other.desc))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((desc == null) ? 0 : desc.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }


    @Override
    public void performWith(final MonitoringEvent e) {
        // TODO Auto-generated method stub

    }
}
