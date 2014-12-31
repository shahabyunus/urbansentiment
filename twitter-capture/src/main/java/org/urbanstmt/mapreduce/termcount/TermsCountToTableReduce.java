package org.urbanstmt.mapreduce.termcount;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.urbanstmt.util.ConstantsAndEnums;
import org.urbanstmt.util.ConstantsAndEnums.AnalysisType;
import org.urbanstmt.util.hbase.HBaseUtility;

import twitter4j.Logger;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.io.Files;

public class TermsCountToTableReduce extends TableReducer<Text, Text, Text> {

	private static final Logger LOG = Logger
			.getLogger(TermsCountToTableReduce.class);

	private List<String> terms;
	private Map<String, Float> termsScore;
	private Table<String, String, Integer> termsCountMap;
	private Table<String, String, List<JSONArray>> lonLatsMap;
	private Text datetimeHour, termsFinalCount;
	private long runTime;
	private JSONParser parser = new JSONParser();

	@Override
	public void setup(TableReducer<Text, Text, Text>.Context context) {

		termsCountMap = HashBasedTable.<String, String, Integer> create();
		lonLatsMap = HashBasedTable.<String, String, List<JSONArray>> create();

		try {
			URI[] paths = context.getCacheFiles();
			if (paths == null || paths.length < 2) {
				LOG.error("No terms files provided in the reducer.");
				throw new IllegalStateException("Terms file missing.");
			}

			LOG.info("paths1=" + paths[0]);
			LOG.info("paths2=" + paths[1]);

			loadTerms();
			loadTermsScore();

		} catch (IOException e) {
			LOG.error("Can't read terms files cached on hdfs, in reducer", e);
			throw new IllegalStateException("Terms file read error", e);
		}

		runTime = context.getConfiguration().getLong(
				ConstantsAndEnums.RUN_TIME_CONFIG,
				System.currentTimeMillis() / 1000);

	}

	private void loadTermsScore() throws IOException {

		this.termsScore = new HashMap<String, Float>();
		File termsFile = new File(TermsCountJob.TERMS_SCORES_FILE_PATH);
		List<String> lines = Files.readLines(termsFile,
				Charset.defaultCharset());
		if (lines == null || lines.size() < 1) {
			LOG.error("No terms score provided in the terms file, in the reducer. Will use default of 1");
			return;
		}
		for (String l : lines) {
			String term[] = l.split(",");
			try {
				Float score = Float.parseFloat(term[1]);
				this.termsScore.put(term[0].toLowerCase(), score);

				LOG.info("term=" + term[0] + ", score=" + term[1]);
			} catch (Exception ex) {
				LOG.error("Error in capturing term score for line (" + l
						+ ") in the terms score files. Wull use default of 1.");
			}
		}
	}

	private void loadTerms() throws IOException {
		File termsFile = new File(TermsCountJob.TERMS_FILE_PATH);
		List<String> lines = Files.readLines(termsFile,
				Charset.defaultCharset());
		if (lines == null || lines.size() < 1) {
			LOG.warn("No terms file provided in the terms score file, in the reducer.");
			throw new IllegalStateException("Terms scores missing.");
		}

		this.terms = new ArrayList<String>();
		for (String l : lines) {
			String term[] = l.split(",");
			for (String t : term) {
				this.terms.add(t);

				LOG.info("term=" + t);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void reduce(Text key, Iterable<Text> values,
			TableReducer<Text, Text, Text>.Context context) throws IOException,
			InterruptedException {

		datetimeHour = new Text();
		termsFinalCount = new Text();

		// Key is datetime till the hour part.
		// It is guaranteed that all same datetime will be
		// send to one reducer and in order.
		// Value is the text of the tweet.
		String k = key.toString();

		for (Text value : values) {

			JSONObject object = null;
			try {
				object = (JSONObject) parser.parse(value.toString());
			} catch (ParseException e) {
				LOG.error("Invalid json value=" + value.toString());
				continue;
			}

			String v = (String) object.get(ConstantsAndEnums.JSON_TEXT_FIELD);
			if (v != null) {
				v = v.toLowerCase();
			} else {
				continue;
			}

			JSONArray lonLatJson = (JSONArray) object
					.get(ConstantsAndEnums.JSON_LONLAT_FIELD);

			Map<String, Integer> termsCountPerDate = termsCountMap.rowMap()
					.get(k);

			Map<String, List<JSONArray>> termsLonLatsPerDate = lonLatsMap.rowMap()
					.get(k);

			if (termsCountPerDate == null) {
				termsCountPerDate = new HashMap<String, Integer>();
			}

			if (termsLonLatsPerDate == null) {
				termsLonLatsPerDate = new HashMap<String, List<JSONArray>>();
			}

			for (String term : this.terms) {
				Integer count = termsCountPerDate.get(term);
				if (count == null) {
					count = 0;
				}

				termsCountMap.put(
						k,
						term,
						count += (StringUtils.countMatches(v,
								term.toLowerCase())));

				if (lonLatJson != null) {
					List<JSONArray> geos = termsLonLatsPerDate.get(term);
					if (geos == null) {
						geos = new ArrayList<JSONArray>();
					}

					geos.add(lonLatJson);
					lonLatsMap.put(k, term, geos);
				}
			}

		}

		// This is per datetime-hour part...final counts
		for (String dateHourPart : termsCountMap.rowKeySet()) {
			Map<String, Integer> termsCountPerDate = termsCountMap.rowMap()
					.get(dateHourPart);

			if (termsCountPerDate == null) {
				continue;
			}

			datetimeHour.set(dateHourPart);
			for (String t : termsCountPerDate.keySet()) {

				termsFinalCount.set(t + ":" + termsCountPerDate.get(t));

				Integer c = termsCountPerDate.get(t);
				Float score = this.termsScore.get(t);
				if (score == null || Float.compare(score, 0) == 0) {
					score = 1f;
				}

				byte[] rowKey = getRowKey(k, t, AnalysisType.TERMS_COUNT);
				Put p = new Put(rowKey);
				p.add(HBaseUtility.ANALYSIS_TC_COL_FAMILY,
						HBaseUtility.TERM_COUNT_SCORE_COL,
						Bytes.toBytes((score * (c == null ? 0 : c))));

				p.add(HBaseUtility.ANALYSIS_TC_COL_FAMILY,
						HBaseUtility.TERM_COUNT_CREATED_COL,
						Bytes.toBytes(this.runTime));

				List<JSONArray> geos = lonLatsMap.get(k, t);
				if (geos != null && geos.size() > 0) {

					p.add(HBaseUtility.ANALYSIS_TC_COL_FAMILY,
							HBaseUtility.TERM_COUNT_REG_LONLAT,
							Bytes.toBytes(JSONValue.toJSONString(geos)));
				}

				context.write(null, p);
			}
		}
	}

	private byte[] getRowKey(String dt, String term, AnalysisType a) {
		byte[] rowKey = null;
		try {
			byte[] dateBytes = Bytes.toBytes(dt);
			byte[] termInBytes = Bytes.toBytes(term);
			byte[] aInBytes = Bytes.toBytes(a.toString());

			int keyLength = dateBytes.length + aInBytes.length
					+ termInBytes.length;
			rowKey = new byte[keyLength];
			ByteBuffer target = ByteBuffer.wrap(rowKey);
			target.put(dateBytes);
			target.put(aInBytes);
			target.put(termInBytes);

		} catch (Exception ex) {
			throw new IllegalStateException("Error creating  row key", ex);
		}

		return rowKey;
	}

	@Override
	public void cleanup(TableReducer<Text, Text, Text>.Context context) {

	}


}