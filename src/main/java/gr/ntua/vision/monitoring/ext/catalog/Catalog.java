package gr.ntua.vision.monitoring.ext.catalog;

import gr.ntua.vision.monitoring.util.Pair;

import java.util.List;


/**
 * The local catalog interface.
 */
public interface Catalog {
    /**
     * <b>LCAT_GetVariable</b>
     * <p>
     * This function retrieves a particular data item that a program component previously saved in the catalog. This interface is
     * provided for various program components to retrieve monitoring and other information from the catalog store. It must be
     * stressed that the catalog is built upon a store using an eventual consistency model, so there are no strong guarantees as
     * to what happens in a distributed environment.
     * </p>
     * 
     * @param <T>
     *            return type.
     * @param key
     *            name of component that owns the specified variable
     * @param var
     *            name of variable containing the data to be retrieved
     * @param type
     *            return type class object.
     * @return the data
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     *             - Errors returned from underlying database
     */
    public <T> T as(String key, String var, Class<T> type) throws IllegalArgumentException, IllegalStateException;


    /**
     * <b>LCAT_DeleteVariableRange</b>
     * <p>
     * This function deletes a range of data items that were saved by a program component in the catalog. This interface is
     * provided for various program components to prune monitoring and other information from the catalog store.
     * </p>
     * 
     * @param key
     *            name of component that owns the specified variable
     * @param min
     *            min value of variable (e.g. time stamp) to be deleted related to Key
     * @param max
     *            max value of variable (e.g. time stamp) to be deleted related to Key
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     *             - Errors returned from underlying database
     */
    public void deleteRange(String key, String min, String max) throws IllegalArgumentException, IllegalStateException;


    /**
     * <b>LCAT_DeleteTimeStampedData</b>
     * <p>
     * This function deletes a range of data items that were saved by a program component in the catalog. This interface is
     * provided for various program components to prune monitoring and other information from the catalog store.
     * </p>
     * 
     * @param key
     *            name of component that owns the specified variable
     * @param min
     *            beginning time stamp from which to delete results
     * @param max
     *            end time stamp from which to delete results
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     *             - Errors returned from underlying database
     */
    public void deleteTimeRange(String key, long min, long max) throws IllegalArgumentException, IllegalStateException;


    /**
     * <b>LCAT_GetAllVariables</b>
     * <p>
     * This function retrieves all the data that a program component previously saved in the catalog. This interface is provided
     * for various program components to retrieve monitoring and other information from the catalog store. It must be stressed
     * that the catalog is built upon a store using an eventual consistency model, so there are no strong guarantees as to what
     * happens in a distributed environment.
     * </p>
     * 
     * @param key
     *            name of component that owns the specified variable
     * @param items
     *            list of &lt;variable:value&gt; pairs containing the data retrieved
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     *             - Errors returned from underlying database
     */
    public void get(String key, List<Pair<String, Object>> items) throws IllegalArgumentException, IllegalStateException;


    /**
     * <b>LCAT_GetVariable</b>
     * <p>
     * This function retrieves a particular data item that a program component previously saved in the catalog. This interface is
     * provided for various program components to retrieve monitoring and other information from the catalog store. It must be
     * stressed that the catalog is built upon a store using an eventual consistency model, so there are no strong guarantees as
     * to what happens in a distributed environment.
     * </p>
     * 
     * @param key
     *            name of component that owns the specified variable
     * @param var
     *            name of variable containing the data to be retrieved
     * @return the data
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     *             - Errors returned from underlying database
     */
    public Object get(String key, String var) throws IllegalArgumentException, IllegalStateException;


    /**
     * <b>LCAT_PutVariables</b>
     * <p>
     * This function saves data in the catalog. This interface is provided for various program components to save monitoring and
     * other information in the catalog store. The variables to be saved should be provided as a list of &lt;variable:value&gt;
     * pairs. New data provided for a variable will overwrite a previously defined value for that variable. It must be stressed
     * that the catalog is built upon a store using an eventual consistency model, so there are no strong guarantees as to what
     * happens in a distributed environment.
     * </p>
     * 
     * @param key
     *            name of component that owns the specified variables to be saved
     * @param items
     *            list of &lt;variable:value&gt; pairs containing the data to be stored. The types allowed for values depend on
     *            the implementation.
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     *             - errors returned from underlying storage.
     */
    public void put(String key, List<Pair<String, Object>> items) throws IllegalArgumentException, IllegalStateException;


    /**
     * <b>LCAT_PutTimeStampedData</b>
     * <p>
     * This function saves extended data in the catalog. This interface is provided for various program components to save
     * monitoring and other information in the catalog store. The intent is for KeyName to indicate a major key while TimeStamp is
     * used to enable range lookups. For each such TimeStamp, a list of &lt;variable:value&gt; pairs is saved. The
     * &lt;variable:value&gt; list may differ for each TimeStamp value, but it is expected that typically they will have much in
     * common.
     * </p>
     * 
     * @param key
     *            name of component that owns the specified variables to be saved
     * @param timestamp
     *            variable on which to perform range queries
     * @param items
     *            list of &lt;variable:value&gt; pairs containing the data to be stored. The types allowed for values depend on
     *            the implementation.
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     *             - errors returned from underlying storage.
     */
    public void put(String key, long timestamp, List<Pair<String, Object>> items) throws IllegalArgumentException,
            IllegalStateException;


    /**
     * <b>LCAT_GetVariableRange</b>
     * <p>
     * This function retrieves a range of data items that were saved by a program component in the catalog. This interface is
     * provided for various program components to retrieve monitoring and other information from the catalog store. It must be
     * stressed that the catalog is built upon a store using an eventual consistency model, so there are no strong guarantees as
     * to what happens in a distributed environment.
     * </p>
     * 
     * @param key
     *            name of component that owns the specified variable
     * @param min
     *            min value of variable (e.g. time stamp) to be retrieved related to Key
     * @param max
     *            max value of variable (e.g. time stamp) to be retrieved related to Key
     * @param results
     *            a list of &lt;variable:value&gt; pairs, where "variable" is in the specified range
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     *             - Errors returned from underlying database
     */
    public void range(String key, String min, String max, List<Pair<String, Object>> results) throws IllegalArgumentException,
            IllegalStateException;


    /**
     * <b>LCAT_GetTimeStampedData</b>
     * <p>
     * This function retrieves extended data in the catalog that was saved according to timestamp (or range) using the the
     * LCAT_PutTimeStampedData interface. This interface is provided for various program components to retrieve monitoring and
     * other information in the catalog store. The intent is for KeyName to indicate a major key while the TimeStamp_t parameters
     * are used to enable range lookups. For each TimeStamp in the specified range, the timestamp with a list of corresponding
     * &lt;variable:value&gt; pairs is returned.
     * </p>
     * 
     * @param key
     *            name of component that owns the specified variable
     * @param min
     *            beginning time stamp from which to return results
     * @param max
     *            end time stamp from which to return results
     * @param results
     *            a list of &lt;variable:value&gt; pairs, where "variable" is in the specified range
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     *             - Errors returned from underlying database
     */
    public void timeRange(String key, long min, long max, List<Pair<Long, List<Pair<String, Object>>>> results)
            throws IllegalArgumentException, IllegalStateException;
}
