/*
 * © Copyright 2015
 * Landcare Research
 * 
 * Dual License with
 * 
 * GPL v3 - See http://www.gnu.org/licenses/gpl-3.0.txt
 * 
 * Any derivative work needs to be contributed back to this project
 * unless otherwise agreed with Landcare Research, New Zealand.
 */
package org.landcare.json;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 * @author heuert@landcareresearch.co.nz
 */
public class JSONPathObject {

	private final JSONObject jsonObject;

	public JSONPathObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	private boolean isJSONArrayProperty(String subPath) {
		return subPath.indexOf("[") != -1;
	}

	private int getJSONIndex(String subPath) {
		String parts[] = subPath.split("\\[");
		int index = Integer.parseInt(parts[1].substring(0, parts[1].length() - 1));
		return index;
	}

	private String getJSONProperty(String subPath) {
		if (isJSONArrayProperty(subPath)) {
			String parts[] = subPath.split("\\[");
			String property = parts[0];
			return property;
		}
		return subPath;
	}

	private String getStringOrArrayValue(JSONObject json, String subPath) {
		if (isJSONArrayProperty(subPath)) {
			// is array
			String property = getJSONProperty(subPath);
			int index = getJSONIndex(subPath);
			return json.getJSONArray(property).getString(index);
		}
		return json.getString(subPath);
	}

	private JSONObject getJSONPath(JSONObject json, String subPath) {
		if (isJSONArrayProperty(subPath)) {
			String property = getJSONProperty(subPath);
			int index = getJSONIndex(subPath);
			return json.getJSONArray(property).getJSONObject(index);
		}
		return json.getJSONObject(subPath);
	}

	/**
	 * Get the value for the given path.
	 *
	 * @param jsonPath e.g. "some.sub[3].property"
	 * @return value at the end of the object
	 */
	public String getStringValue(String jsonPath) {
		String parts[] = jsonPath.split("\\.");
		int count = 0;
		JSONObject json = jsonObject;
		JSONArray jsonArray = null;
		for (String subPath : parts) {
			count++;
			if (count == parts.length) {
				return getStringOrArrayValue(json, subPath);
			} else {
				json = getJSONPath(json, subPath);
			}
		}
		return json.getString(jsonPath);
	}

	public static void main(String[] args) {
		// simplest test
		JSONObject json = JSONObject.fromObject("{\"sub\":[{\"prop\":[\"world\",\"good\"]}]}");
		System.out.println("" + (new JSONPathObject(json)).getStringValue("sub[0].prop[1]"));
	}

	// TODO: getIntValue() etc
}
