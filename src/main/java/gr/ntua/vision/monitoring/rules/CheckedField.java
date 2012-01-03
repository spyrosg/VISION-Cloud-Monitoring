package gr.ntua.vision.monitoring.rules;

/**
 * this specifies the structure of fields which can be checked against constants or regular expressions.
 */
public interface CheckedField {
    /**
     * get the value of the field.
     * 
     * @param source
     *            source object (which contains the field).
     * @return the value of the field.
     */
    public Object fieldValue(Object source);


    /**
     * check if this field contains inner fields.
     * 
     * @return <code>true</code> if and only if this field contains inner fields.
     */
    public boolean hasInner();
}
