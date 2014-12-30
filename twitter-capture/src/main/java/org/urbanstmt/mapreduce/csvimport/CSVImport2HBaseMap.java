package org.urbanstmt.mapreduce.csvimport;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.urbanstmt.exception.TweetStorageException;
import org.urbanstmt.util.ConstantsAndEnums;
import org.urbanstmt.util.UtilFunctions;
import org.urbanstmt.util.ConstantsAndEnums.DataDomainType;
import org.urbanstmt.util.hbase.HBaseUtility;

import twitter4j.Logger;

public class CSVImport2HBaseMap extends
		Mapper<LongWritable, Text, ImmutableBytesWritable, Put> {

	private static final Logger LOG = Logger
			.getLogger(CSVImport2HBaseMap.class);

	// Only for single threaded use
	private static SimpleDateFormat format;

	private static final long txnTime = System.currentTimeMillis();

	ImmutableBytesWritable writeKey = new ImmutableBytesWritable();

	@Override
	public void setup(
			Mapper<LongWritable, Text, ImmutableBytesWritable, Put>.Context c) {
		format = new SimpleDateFormat(
				ConstantsAndEnums.CSV_IMPOR_DATETIME_FORMAT);
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Override
	public void map(
			LongWritable row,
			Text value,
			Mapper<LongWritable, Text, ImmutableBytesWritable, Put>.Context context)
			throws IOException, InterruptedException {

		String[] columns = value.toString().split(",");

		if (columns == null || columns.length < 3) {
			LOG.error("Line of text has less than 3 columns. At least tweetid, text and time are required="
					+ value);
			return;
		}

		String tweetIdStr = columns[0];
		String text = columns[1];
		String utcDateTime = columns[2];

		long tweetId = -1;
		try {
			tweetId = Long.parseLong(UtilFunctions.stripQuotes(tweetIdStr));
		} catch (NumberFormatException nfe) {
			LOG.error("Error parseing tweetId=" + tweetId, nfe);
			return;
		}

		long createdAt = -1;
		try {
			// e.g. date: "Tue Nov 26 02:59:01 UTC 2013"
			createdAt = format.parse(UtilFunctions.stripQuotes(utcDateTime)).getTime();
		} catch (ParseException pe) {
			LOG.error("Error parseing tweet creation time=" + utcDateTime, pe);
			return;
		}

		byte[] rowKey;
		try {
			rowKey = HBaseUtility.buildRowKey(tweetId, txnTime,
					DataDomainType.TWITTER);
		} catch (TweetStorageException tse) {
			LOG.error(
					"Error creating row key for the HBase table row with fields="
							+ (tweetId + "," + txnTime + "," + DataDomainType.TWITTER),
					tse);
			return;
		}
		Put p = new Put(rowKey);

		if (columns.length > 3) {
			String username = columns[3];
			if (!StringUtils.isEmpty(username)) {
				p.add(HBaseUtility.TWEET_COL_FAMILY,
						HBaseUtility.TWEET_USERNAME_COL,
						Bytes.toBytes(UtilFunctions.stripQuotes(username)));
			}
		}

		String userId = null;
		if (columns.length > 4) {
			userId = columns[4];

			try {
				p.add(HBaseUtility.TWEET_COL_FAMILY,
						HBaseUtility.TWEET_USERID_COL,
						Bytes.toBytes(Long.parseLong(UtilFunctions.stripQuotes(userId))));
			} catch (NumberFormatException nfe) {
				LOG.error("Error parsing user id to long. Ignoring the field="
						+ userId, nfe);
			}

		}

		if (columns.length > 5) {
			String cityName = columns[5];
			if (!StringUtils.isEmpty(cityName)) {
				p.add(HBaseUtility.TWEET_COL_FAMILY,
						HBaseUtility.TWEET_PL_CITYNAME_COL,
						Bytes.toBytes(UtilFunctions.stripQuotes(cityName)));
			}
		}

		p.add(HBaseUtility.TWEET_COL_FAMILY, HBaseUtility.TWEET_TEXT_COL,
				Bytes.toBytes(UtilFunctions.stripQuotes(text)));
		p.add(HBaseUtility.TWEET_COL_FAMILY, HBaseUtility.TWEET_CREATEDAT_COL,
				Bytes.toBytes(createdAt));

		writeKey.set(rowKey);
		context.write(writeKey, p);

	}
	

}
