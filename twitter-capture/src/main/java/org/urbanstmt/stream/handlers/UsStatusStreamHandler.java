package org.urbanstmt.stream.handlers;

import org.urbanstmt.exception.TweetStatusParseException;
import org.urbanstmt.exception.twitter.TweetStorageException;
import org.urbanstmt.model.twitter.TweetVO;
import org.urbanstmt.parser.twitter.JsonTweetStatusParser;
import org.urbanstmt.parser.twitter.TweetStatusParser;
import org.urbanstmt.storage.TweetStorage;
import org.urbanstmt.storage.hbase.HBaseTweetStorage;

import twitter4j.Logger;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;

import com.twitter.hbc.twitter4j.handler.StatusStreamHandler;
import com.twitter.hbc.twitter4j.message.DisconnectMessage;
import com.twitter.hbc.twitter4j.message.StallWarningMessage;

public class UsStatusStreamHandler implements StatusStreamHandler {

	private static final Logger LOG = Logger
			.getLogger(UsStatusStreamHandler.class);

	private final TweetStatusParser parser;
	private final TweetStorage storage;

	public UsStatusStreamHandler() {
		parser = new JsonTweetStatusParser();
		storage = new HBaseTweetStorage();
	}

	public UsStatusStreamHandler(TweetStatusParser parser) {
		this(new HBaseTweetStorage(), parser);
	}

	public UsStatusStreamHandler(TweetStorage storage) {

		this(storage, new JsonTweetStatusParser());
	}

	public UsStatusStreamHandler(TweetStorage storage, TweetStatusParser parser) {
		this.parser = parser;
		this.storage = storage;
	}

	@Override
	public void onStatus(Status status) {

		if (LOG.isDebugEnabled()) {
			LOG.debug(status.toString());
		}
		LOG.debug(status.toString());

		if(parser == null)
		{
			throw new IllegalStateException("No parser specified.");
		}
		
		if(storage == null)
		{
			throw new IllegalStateException("No storage specified.");
		}
		
		TweetVO t;
		try {
			t = parser.parse(status);

			if(LOG.isDebugEnabled())
			{
				LOG.debug(t.toString());
			}
			
			storage.storeTweet(t);
		} catch (TweetStatusParseException e) {
			LOG.error("Error parsing tweet status="+status, e);
			throw new IllegalStateException(e);
		} catch (TweetStorageException e) {
			LOG.error("Error storing tweet status="+status, e);
			throw new IllegalStateException(e);
		}
		
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
	}

	@Override
	public void onTrackLimitationNotice(int limit) {
		System.out.print("onTrackLimitationNotice=" + limit);
		LOG.info("onTrackLimitationNotice=" + limit);
	}

	@Override
	public void onScrubGeo(long user, long upToStatus) {
	}

	@Override
	public void onStallWarning(StallWarning warning) {
		System.out.print(warning);
		LOG.warn(warning.toString());
	}

	@Override
	public void onException(Exception e) {
		System.out.print(e.getMessage());
		LOG.error("Error", e);
	}

	@Override
	public void onDisconnectMessage(DisconnectMessage message) {
		System.out.print(message);
		LOG.info(message.toString());
	}

	@Override
	public void onStallWarningMessage(StallWarningMessage warning) {
		System.out.print(warning);
		LOG.warn(warning.toString());
	}

	@Override
	public void onUnknownMessageType(String s) {
		System.out.print(s);
		LOG.warn("Unknown message type=" + s);
	}

}
