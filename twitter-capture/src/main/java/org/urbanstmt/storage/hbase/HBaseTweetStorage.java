package org.urbanstmt.storage.hbase;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.urbanstmt.exception.twitter.TweetStorageException;
import org.urbanstmt.model.twitter.TweetVO;
import org.urbanstmt.storage.TweetStorage;
import org.urbanstmt.util.ConstantsAndEnums.DataDomainType;
import org.urbanstmt.util.hbase.HBaseUtility;

import twitter4j.Logger;

import com.google.common.base.Strings;

public class HBaseTweetStorage implements TweetStorage {

	private static final Logger LOG = Logger.getLogger(HBaseTweetStorage.class);
	private static final String CONFIG_FILE = "hbase.properties";
	private final Configuration conf;
	private static final Properties p = new Properties();
	private final String DATA_TABLE_NAME;

	public HBaseTweetStorage() {

		InputStream stream = HBaseTweetStorage.class.getClassLoader()
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

	@Override
	public void storeTweet(TweetVO tweet) throws TweetStorageException {

		HTable table = null;
		byte[] rowKey = HBaseUtility.buildRowKey(tweet.getId(), tweet.getTxn_time(),
				DataDomainType.TWITTER);
		Put put = new Put(rowKey);
		try {
			
			System.out.println(tweet.getCoords());
			HBaseUtility.populateTweetPuts(tweet, put);
			System.out.println(put);
		} catch (InstantiationException | IllegalAccessException e1) {
			LOG.error(
					"There was an error while copying data from object to hbase puts.",
					e1);
			throw new TweetStorageException(e1);
		}
		try {
			table = new HTable(conf, DATA_TABLE_NAME);
			table.put(put);
		} catch (Exception e) {
			LOG.error(
					"There was an error while inserting data in hbase table.",
					e);
			throw new TweetStorageException(e);
		} finally {
			if (table != null) {
				try {
					table.close();
				} catch (IOException e) {
					LOG.error("There was an error while closing hbase table.",
							e);
					throw new TweetStorageException(e);
				}
			}
		}

	}

}
