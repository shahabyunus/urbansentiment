package org.urbanstmt.analytics.store;

import org.json.simple.JSONObject;
import org.urbanstmt.exception.AnalyticsStoreException;

public interface AnalyticsStore {
	
	//For 'term count' type analysis
	public JSONObject getTermCountResults(String stime, String etime) throws AnalyticsStoreException;
}
