package org.urbanstmt.analytics.store;

import org.json.simple.JSONObject;

public interface AnalyticsStore {
	
	//For 'term count' type analysis
	public JSONObject getTermCountResults(String stime, String etime);
}
