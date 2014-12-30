package org.urbanstmt.mapreduce.csvimport;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import twitter4j.Logger;

public class CSVImport2HBaseJob extends Configured implements Tool {

	private static final Logger LOG = Logger
			.getLogger(CSVImport2HBaseJob.class);

	private final String tableName;
	private final String inputPathFileName;

	public CSVImport2HBaseJob(Configuration conf, String inputPathFileName,
			String tableName) {
		super(conf);
		this.inputPathFileName = inputPathFileName;
		this.tableName = tableName;
	}

	@Override
	public int run(String[] args) throws Exception {
		return runJob();
	}

	public int runJob() throws Exception {

		Configuration conf = getConf();
		Job job = Job.getInstance(conf);

		job.setJarByClass(CSVImport2HBaseJob.class);

		Path inputPath = new Path(this.inputPathFileName);
		FileInputFormat.setInputPaths(job, inputPath);
		job.setInputFormatClass(TextInputFormat.class);
		job.setMapperClass(CSVImport2HBaseMap.class);
		job.setMapOutputKeyClass(ImmutableBytesWritable.class);
		job.setMapOutputValueClass(Put.class);

		TableMapReduceUtil.initTableReducerJob(tableName, null, job);
		boolean success = job.waitForCompletion(true);
		return success ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {

		if (args.length < 2) {
			LOG.error("The mandatory parameters in this particular order are= <input csv file path> <target table name>\n");
			throw new IllegalStateException("Invalid or incomplete parameters.");
		}

		String inputPathFileName = args[0];
		String tableName = args[1];

		LOG.info("Table name=" + tableName);
		LOG.info("Source csv file=" + inputPathFileName);

		Configuration conf = new Configuration();
		int exitCode = ToolRunner.run(conf, new CSVImport2HBaseJob(conf,
				inputPathFileName, tableName), args);
		System.exit(exitCode);
	}
}