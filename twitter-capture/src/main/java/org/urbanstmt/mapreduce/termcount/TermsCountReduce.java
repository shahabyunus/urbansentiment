package org.urbanstmt.mapreduce.termcount;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import twitter4j.Logger;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.io.Files;

public class TermsCountReduce extends Reducer<Text, Text, Text, Text> {

	private static final Logger LOG = Logger.getLogger(TermsCountReduce.class);

	private List<String> terms;
	private Map<String, Float> termsScore;
	private Table<String, String, Integer> termsCountMap;
	private Text datetimeHour, termsFinalCount;

	@Override
	public void setup(Reducer<Text, Text, Text, Text>.Context context) {

		termsCountMap = HashBasedTable.<String, String, Integer> create();

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

	@Override
	public void reduce(Text key, Iterable<Text> values,
			Reducer<Text, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {

		datetimeHour = new Text();
		termsFinalCount = new Text();

		// Key is datetime till the hour part.
		// It is guaranteed that all same datetime will be
		// send to one reducer and in order.
		// Value is the text of the tweet.
		String k = key.toString();

		for (Text value : values) {

			String v = value.toString().toLowerCase();

			Map<String, Integer> termsCountPerDate = termsCountMap.rowMap()
					.get(k);

			if (termsCountPerDate == null) {
				termsCountPerDate = new HashMap<String, Integer>();
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
				Float score = this.termsScore.get(t);
				if (score == null || Float.compare(score, 0) == 0) {
					score = 1f;
				}

				Integer c = termsCountPerDate.get(t);
				c = (c == null ? 0 : c);
				termsFinalCount.set(t + ":" + c + ":" + (score * c));
				context.write(datetimeHour, termsFinalCount);
			}
		}
	}

	@Override
	public void cleanup(Reducer<Text, Text, Text, Text>.Context context) {

	}

}