package gr.ntua.vision.monitoring.rules;

import java.util.ArrayList;

import org.json.simple.JSONObject;


/**
 * 
 */
public class CTOTenantObject {
    /***/
    private final String                        tenant;
    /***/
    private final ArrayList<CTOContainerObject> containers = new ArrayList<CTOContainerObject>();


    /**
     * Constructor.
     * 
     * @param tenant
     */
    public CTOTenantObject(String tenant) {
        this.tenant = tenant;
    }


    /**
     * @param cont
     */
    public void addContainer(final CTOContainerObject cont) {
        containers.add(cont);
    }


    /**
     * @return
     */
    @SuppressWarnings("unchecked")
    public String toJSONString() {
        JSONObject obj = new JSONObject();

        obj.put("name", tenant);
        obj.put("containers", containers);

        return obj.toString();
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toJSONString();
    }
}
