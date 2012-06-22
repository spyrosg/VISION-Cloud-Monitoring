package gr.ntua.vision;

import java.util.Set;


/**
 * A configuration is just a sequence of key/value pairs.
 */
public interface Configuration {
    /**
     * Get the value for the given key.
     * 
     * @param key
     *            the key.
     * @return the corresponding value in the configuration, or <code>null</code> if the key is not found in the configuration.
     */
    String get(String key);


    /**
     * @return the available configuration keys.
     */
    Set<String> keys();


    /**
     * Update the configuration key with given value.
     * 
     * @param key
     *            the key to update.
     * @param val
     *            the new value.
     */
    void put(String key, String val);
}
