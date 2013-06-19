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
     * @param i
     * @return the violation at index.
     */
    public Violation get(final int i) {
        return list.get(i);
    }


    /**
     * @return the list size.
     */
    public int size() {
        return list.size();
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return list.toString();
    }
}
