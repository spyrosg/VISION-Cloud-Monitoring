package gr.ntua.vision.monitoring.rules;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 */
public class ViolationsList {
    /***/
    private final List<Violation> list;


    /**
     * Constructor.
     */
    public ViolationsList() {
        this.list = new ArrayList<Violation>();
    }


    /**
     * @param v
     */
    public void add(final Violation v) {
        list.add(v);
    }


    /**
     * @return the list size.
     */
    public int size() {
        return list.size();
    }
}
