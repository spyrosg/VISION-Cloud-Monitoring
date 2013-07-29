package gr.ntua.vision.monitoring.resources;

import java.util.ArrayList;


/**
 *
 */
public class RuleListBean {
    /***/
    private ArrayList<RuleBean> rules;


    /**
     * Constructor.
     */
    public RuleListBean() {
    }


    /**
     * @return the rules
     */
    public ArrayList<RuleBean> getRules() {
        return rules;
    }


    /**
     * @param rules
     *            the rules to set
     */
    public void setRules(final ArrayList<RuleBean> rules) {
        this.rules = rules;
    }
}
