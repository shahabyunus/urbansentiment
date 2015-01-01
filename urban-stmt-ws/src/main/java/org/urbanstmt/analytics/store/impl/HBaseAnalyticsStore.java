package org.urbanstmt.analytics.store.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urbanstmt.analytics.model.TermCountRow;
import org.urbanstmt.analytics.model.TermCountRow.LonLatPair;
import org.urbanstmt.analytics.store.AnalyticsStore;
import org.urbanstmt.exception.AnalyticsStoreException;
import org.urbanstmt.util.ConstantsAndEnums;
import org.urbanstmt.util.hbase.HBaseUtility;

import com.google.common.base.Strings;

public class HBaseAnalyticsStore implements AnalyticsStore {

	private static final Logger LOG = LoggerFactory
			.getLogger(HBaseAnalyticsStore.class.getName());
	private static final String CONFIG_FILE = "hbase.properties";
	private final Configuration conf;
	private static final Properties p = new Properties();
	private final String DATA_TABLE_NAME;

	final private static int KEY_FIELD1_LEN = Bytes.toBytes("2000010100").length;
	final private static int KEY_FIELD2_LEN = Bytes
			.toBytes(ConstantsAndEnums.AnalysisType.TERMS_COUNT.toString()).length;
	final private static int MIN_KEY_LEN = KEY_FIELD1_LEN + KEY_FIELD2_LEN;

	final static private ThreadLocal<JSONParser> parser = new ThreadLocal<JSONParser>();

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

		LOG.info("Hbase Table name=" + tableName);
		DATA_TABLE_NAME = tableName;
		conf = HBaseConfiguration.create();
		parser.set(new JSONParser());
	}

	@Override
	public List<TermCountRow> getTermCountResults(String stime, String etime)
			throws AnalyticsStoreException {
		HTable table = null;
		List<TermCountRow> rows = new ArrayList<TermCountRow>();

		try {
			table = new HTable(conf, DATA_TABLE_NAME);
			byte[] startRow = HBaseUtility.buildPartialAnalyticsTCRowKey(stime);
			byte[] stopRow = HBaseUtility.buildPartialAnalyticsTCRowKey(etime);

			Scan scan = new Scan();
			scan.setStartRow(startRow);
			scan.setStopRow(stopRow);

			scan.setCaching(1000);
			scan.setCacheBlocks(true);

			ResultScanner result = table.getScanner(scan);
			byte[] cellValue = null;

			TermCountRow row = null;

			for (Result r : result) {
				byte[] rowKey = r.getRow();
				row = new TermCountRow();
				populateRowKeyFields(rowKey, row);

				cellValue = r.getValue(HBaseUtility.ANALYSIS_TC_COL_FAMILY,
						HBaseUtility.TERM_COUNT_REG_LONLAT);
				if (cellValue != null) {
					populateLonLats(cellValue, row);
				}

				cellValue = r.getValue(HBaseUtility.ANALYSIS_TC_COL_FAMILY,
						HBaseUtility.TERM_COUNT_SCORE_COL);
				populateScore(cellValue, row);

				rows.add(row);

			}

		} catch (Exception ex) {
			LOG.error("Error occured while trying to read analytics results. Parameters passed...stime="
					+ stime + ", etime=" + etime);
			throw new AnalyticsStoreException(ex);
		} finally {
			if (table != null) {
				try {
					table.close();
				} catch (IOException e) {
					LOG.error("There was an error while closing hbase table.",
							e);
					throw new AnalyticsStoreException(e);
				}
			}
		}
		return rows;
	}

	private void populateScore(byte[] cellValue, TermCountRow row) {
		if (cellValue == null) {
			row.setScore(0);
			return;
		}
		try {
			float score = Bytes.toFloat(cellValue);
			row.setScore(score);
		} catch (Exception ex) {
			row.setScore(0);
		}
	}

	private void populateLonLats(byte[] cellValue, TermCountRow row) {
		String regionInfo = null;
		JSONArray lonLatsArray = null;
		try {
			regionInfo = Bytes.toString(cellValue);
			lonLatsArray = (JSONArray) parser.get().parse(regionInfo);

			int s = lonLatsArray.size();
			List<LonLatPair> lonLatPairs = new ArrayList<LonLatPair>(s);
			for (int index = 0; index < s; index++) {
				JSONArray lonLatArray = (JSONArray) lonLatsArray.get(index);
				if (lonLatArray != null && lonLatArray.size() > 1) {
					try {
						float lon = ((Number) lonLatArray.get(0)).floatValue();
						float lat = ((Number) lonLatArray.get(1)).floatValue();

						lonLatPairs.add(new LonLatPair(lon, lat));
					} catch (Exception ex) {
						LOG.error("This pair of lon/lat=" + lonLatArray
								+ " for row=[" + row.getHour() + ","
								+ row.getTerm()
								+ "] is not valid. Error="+ex.getMessage()+".Skipping it.");
					}
				}
			}

			row.setLonLats(lonLatPairs);
		} catch (ParseException pe) {
			LOG.error("Invalid lon/lat info for this row=" + regionInfo
					+ ". Going to skip it.");
		}
	}

	private void populateRowKeyFields(byte[] k, TermCountRow r)
			throws AnalyticsStoreException {
		// Our key is stored as chunks of strings so we are ok here.
		String strKey = Bytes.toString(k);

		if (strKey.length() < MIN_KEY_LEN) {
			throw new AnalyticsStoreException("Invalid row key=" + strKey);
		}

		r.setHour(strKey.substring(0, KEY_FIELD1_LEN));

		if (strKey.length() > MIN_KEY_LEN) {
			r.setTerm(strKey.substring(MIN_KEY_LEN));
		}

	}
}
