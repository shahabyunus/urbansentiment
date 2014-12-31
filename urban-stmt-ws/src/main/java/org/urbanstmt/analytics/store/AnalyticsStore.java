package org.urbanstmt.analytics.store;

import java.util.List;

import org.urbanstmt.analytics.model.TermCountRow;
import org.urbanstmt.exception.AnalyticsStoreException;

public interface AnalyticsStore {
	
	//For 'term count' type analysis
	public List<TermCountRow> getTermCountResults(String stime, String etime) throws AnalyticsStoreException;
}
