package org.urbanstmt.util;

public class ConstantsAndEnums {
	
	public final static String SIMPLE_DATETIME_FORMAT = "yyyy-MM-dd-HH:mm:ss";
	public final static String CSV_IMPOR_DATETIME_FORMAT = "EEE MMM dd hh:mm:ss Z yyyy";
	public final static String WS_DATE_RANGE_FORMAT = "yyyyMMddhh";

	
	public final static String JSON_TEXT_FIELD = "text";
	public final static String JSON_LONLAT_FIELD = "lonlat";
	public final static String JSON_LON_FIELD = "lon";
	public final static String JSON_LAT_FIELD = "lat";
	
	public final static String RUN_TIME_CONFIG = "job.runtime";
	
	public static enum DataDomainType	{
		TWITTER("T"), FACEBOOK("F");
		
		private String code;
		DataDomainType(String code)
		{
			this.code = code;
		}
		
		@Override
		public String toString()
		{
			return code;
		}
	}
	
	public static enum AnalysisType	{
		TERMS_COUNT("TC");
		
		private String code;
		AnalysisType(String code)
		{
			this.code = code;
		}
		
		@Override
		public String toString()
		{
			return code;
		}
	}
}
