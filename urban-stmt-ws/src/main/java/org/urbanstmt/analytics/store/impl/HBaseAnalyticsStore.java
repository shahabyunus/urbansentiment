package org.urbanstmt.analytics.store.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.json.simple.JSONObject;
import org.urbanstmt.analytics.store.AnalyticsStore;


import com.google.common.base.Strings;

public class HBaseAnalyticsStore implements AnalyticsStore {
	
	private static final Logger LOG = Logger.getLogger(HBaseAnalyticsStore.class.getName());
	private static final String CONFIG_FILE = "hbase.properties";
	private final Configuration conf;
	private static final Properties p = new Properties();
	private final String DATA_TABLE_NAME;

	public HBaseAnalyticsStore() {

		InputStream stream = HBaseAnalyticsStore.class.getClassLoader()
				.getResourceAsStream(CONFIG_FILE);
		try {
			p.load(stream);
			if (stream == null) {
				throw new IllegalStateException("Config file " + CONFIG_FILE
						+ " is missing.");
			}
		} catch (IOException e) {
			throw new IllegalStateException("Error reading config file "
					+ CONFIG_FILE);
		}

		String tableName = (String) p.get("hbase.table.name");
		if (Strings.isNullOrEmpty(tableName)) {
			tableName = "raw_data";
		}

		LOG.info("Hbase Table name="+tableName);
		DATA_TABLE_NAME = tableName;
		conf = HBaseConfiguration.create();
	}


	public JSONObject getTermCountResults(String stime, String etime) {
		// TODO Auto-generated method stub
		return null;
	}

}
