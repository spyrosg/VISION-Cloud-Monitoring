package gr.ntua.vision.monitoring.model;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * This marks classes which can be exchanged using the JSON format. Implementations of this interface are expected to have a
 * constructor or static instatiator from a {@link JSONObject} instance.
 */
public interface JSONExchanged
{
	/**
	 * convert this to a JSON object.
	 * 
	 * @return the JSON form of this.
	 * @throws JSONException
	 */
	public JSONObject toJSON() throws JSONException;
}
