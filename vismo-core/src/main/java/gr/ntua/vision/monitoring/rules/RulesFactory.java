package gr.ntua.vision.monitoring.rules;

import gr.ntua.vision.monitoring.resources.RuleBean;


/**
 * This is used to abstract away the details of loading rules into the system.
 */
public interface RulesFactory {
    /**
     * @param bean
     * @return a constucted, ready to run {@link VismoRule}.
     */
    VismoRule buildFrom(RuleBean bean);


    /**
     * @return the next factory in the chain.
     */
    RulesFactory next();
}
