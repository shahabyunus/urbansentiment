package org.urbanstmt.mapreduce.termcount;

import java.io.IOException;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.urbanstmt.util.ConstantsAndEnums;
import org.urbanstmt.util.hbase.HBaseUtility;

import twitter4j.Logger;

public class TermsCountMap extends TableMapper<Text, Text> {

	private static final Logger LOG = Logger
			.getLogger(TermsCountMap.class);
	Text valueText = new Text();
	Text keyText = new Text();

	@SuppressWarnings("unchecked")
	@Override
	public void map(ImmutableBytesWritable row, Result value,
			TableMapper<Text, Text>.Context context) throws IOException,
			InterruptedException {

		// Extract only datetime value till hour.
		ImmutableBytesWritable timeBytes = new ImmutableBytesWritable(
				row.get(), 0, Bytes.SIZEOF_LONG);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(Bytes.toLong(timeBytes.get()));
		StringBuilder timeKey = new StringBuilder();

		timeKey.append(c.get(Calendar.YEAR))
				.append(StringUtils.leftPad(
						String.valueOf(c.get(Calendar.MONTH) + 1), 2, "0"))
				.append(StringUtils.leftPad(
						String.valueOf(c.get(Calendar.DAY_OF_MONTH)), 2, "0"))
				.append(StringUtils.leftPad(
						String.valueOf(c.get(Calendar.HOUR)), 2, "0"));
		keyText.set(timeKey.toString());

		// We should only have text of the tweet in the value.
		byte[] textBytes = value.getValue(HBaseUtility.TWEET_COL_FAMILY,
				HBaseUtility.TWEET_TEXT_COL);
		
		byte[] lonBytes = value.getValue(HBaseUtility.TWEET_COL_FAMILY,
				HBaseUtility.TWEET_LON_COL);
		
		byte[] latBytes = value.getValue(HBaseUtility.TWEET_COL_FAMILY,
				HBaseUtility.TWEET_LAT_COL);
		
		JSONObject j = null;
		JSONArray lonLatJson = null;
		if (textBytes != null && textBytes.length > 0) {
			
			j = new JSONObject();
			j.put(ConstantsAndEnums.JSON_TEXT_FIELD, Bytes.toString(textBytes));
			
			if(lonBytes != null && latBytes != null)	{
				lonLatJson = new JSONArray();
				lonLatJson.add(Bytes.toFloat(lonBytes));
				lonLatJson.add(Bytes.toFloat(latBytes));
				j.put(ConstantsAndEnums.JSON_LONLAT_FIELD, lonLatJson.toJSONString());
			}
			
			valueText.set(j.toString());
			context.write(keyText, valueText);
		}
	}

}
