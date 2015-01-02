package org.urbanstmt.mapreduce.termcount;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.TimeZone;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.urbanstmt.util.ConstantsAndEnums;
import org.urbanstmt.util.UtilFunctions;
import org.urbanstmt.util.hbase.HBaseUtility;

import twitter4j.Logger;

public class TermsCountJob extends Configured implements Tool {

	private static final Logger LOG = Logger.getLogger(TermsCountJob.class);

	private final String tableName;
	private final Date stDate;
	private final Date enDate;
	private final String inputTermsPath;

	private final static String OUTPUT_PATH = "output";
	private final static String RES_ANALYSIS_TABLE_NAME = "tc_result";

	public final static String TERMS_FILE_PATH = "terms.txt";
	public final static String TERMS_SCORES_FILE_PATH = "termsscores.txt";

	public TermsCountJob(Configuration conf, Date stDate, Date enDate,
			String terms, String tableName) {
		super(conf);
		this.stDate = stDate;
		this.enDate = enDate;
		this.inputTermsPath = terms;
		this.tableName = tableName;
	}

	@Override
	public int run(String[] args) throws Exception {
		return runJob();
	}

	public int runJob() throws Exception {

		Configuration conf = getConf();
		Job job = Job.getInstance(conf);

		job.setJarByClass(TermsCountJob.class);
		job.getConfiguration().setLong(ConstantsAndEnums.RUN_TIME_CONFIG,
				System.currentTimeMillis() / 1000);

		Scan scan = new Scan();

		byte[] rowKey = getPartialRowKey(this.stDate);
		scan.setStartRow(rowKey);
		byte[] endRowyKey = getPartialRowKey(this.enDate);
		scan.setStopRow(endRowyKey);

		scan.setCaching(10);
		scan.setCacheBlocks(false);

		scan.addColumn(HBaseUtility.TWEET_COL_FAMILY,
				HBaseUtility.TWEET_TEXT_COL);
		scan.addColumn(HBaseUtility.TWEET_COL_FAMILY,
				HBaseUtility.TWEET_LON_COL);
		scan.addColumn(HBaseUtility.TWEET_COL_FAMILY,
				HBaseUtility.TWEET_LAT_COL);

		/*
		 * scan.setFilter(new SingleColumnValueFilter( new byte[1], new byte[1],
		 * CompareOp.EQUAL, Bytes .toBytes(this.batchLoadTime)));
		 */

		TableMapReduceUtil.initTableMapperJob(this.tableName, scan,
				TermsCountMap.class, Text.class, Text.class, job);

		TableMapReduceUtil.initTableReducerJob(RES_ANALYSIS_TABLE_NAME,
				TermsCountToTableReduce.class, job);

		/*
		 * job.setReducerClass(TermsCountReduce.class);
		 * FileOutputFormat.setOutputPath(job, new
		 * Path(System.currentTimeMillis() + "/" + OUTPUT_PATH));
		 * job.setMapOutputKeyClass(Text.class);
		 * job.setMapOutputValueClass(Text.class);
		 * job.setOutputFormatClass(TextOutputFormat.class);
		 */

		job.setNumReduceTasks(3);

		FileSystem fs = FileSystem.get(getConf());
		addTerms(job, fs);
		addTermsScore(job, fs);

		boolean success = job.waitForCompletion(true);

		return success ? 0 : 1;
	}

	private void addTerms(Job job, FileSystem fs) throws IOException {
		Path termsPath = new Path(TERMS_FILE_PATH);
		if (fs.exists(termsPath)) {
			fs.delete(termsPath, true);
		}

		Path termsInputPath = new Path(this.inputTermsPath);
		if (!fs.exists(termsInputPath)) {
			throw new IOException("Input file for terms does not exist at="
					+ termsInputPath);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				fs.open(termsInputPath)));

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				fs.create(termsPath, true)));

		String line;
		while ((line = br.readLine()) != null) {

			bw.write(line.trim().toLowerCase());
			bw.write(",");
		}

		bw.close();
		br.close();

		job.addCacheFile(termsPath.toUri());
	}

	private void addTermsScore(Job job, FileSystem fs) throws IOException {
		Path termsScoresPath = new Path(TERMS_SCORES_FILE_PATH);
		job.addCacheFile(termsScoresPath.toUri());
	}

	public byte[] getPartialRowKey(Date d) {
		byte[] rowKey = null;
		try {
			byte[] dateBytes = Bytes.toBytes(d.getTime());
			byte[] domainType = Bytes
					.toBytes(ConstantsAndEnums.DataDomainType.TWITTER
							.toString());

			int keyLength = dateBytes.length + domainType.length;
			rowKey = new byte[keyLength];
			ByteBuffer target = ByteBuffer.wrap(rowKey);
			target.put(dateBytes);
			target.put(domainType);

		} catch (Exception ex) {
			throw new IllegalStateException("Error creating  row key", ex);
		}

		return rowKey;
	}

	public static void main(String[] args) throws Exception {

		if (args.length < 4) {
			LOG.error("The mandatory parameters in this particular order are= <start date> <end date> <comma-separated list of terms> <source hbase table name>\n"
					+ "The date time format="
					+ ConstantsAndEnums.SIMPLE_DATETIME_FORMAT);
			throw new IllegalStateException("Invalid or incomplete parameters.");
		}

		Date stDate, enDate;
		try {
			stDate = UtilFunctions.createDate(args[0],
					ConstantsAndEnums.SIMPLE_DATETIME_FORMAT,
					TimeZone.getDefault());
		} catch (Exception ex) {

			LOG.error("Provided start date is invalid=" + args[0]
					+ ", it should be in the format="
					+ ConstantsAndEnums.SIMPLE_DATETIME_FORMAT);
			throw new IllegalStateException("Invalid dates");
		}

		try {
			enDate = UtilFunctions.createDate(args[1],
					ConstantsAndEnums.SIMPLE_DATETIME_FORMAT,
					TimeZone.getDefault());
		} catch (Exception ex) {

			LOG.error("Provided start date is invalid=" + args[1]
					+ ", it should be in the format="
					+ ConstantsAndEnums.SIMPLE_DATETIME_FORMAT);
			throw new IllegalStateException("Invalid dates");
		}

		if (stDate.after(enDate)) {
			LOG.error("Invalid dates. Start date is after end date= " + stDate
					+ " > " + enDate);
			throw new IllegalStateException("Invalid dates");
		}

		String inputTermsPath = args[2];
		String tableName = args[3];

		LOG.info("Start date=" + stDate);
		LOG.info("End date=" + enDate);
		LOG.info("Table name=" + tableName);
		LOG.info("Search terms are=" + inputTermsPath);

		Configuration conf = new Configuration();
		int exitCode = ToolRunner.run(conf, new TermsCountJob(conf, stDate,
				enDate, inputTermsPath, tableName), args);
		System.exit(exitCode);
	}
}