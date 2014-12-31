package org.urbanstmt.storage;

import org.urbanstmt.exception.twitter.TweetStorageException;
import org.urbanstmt.model.twitter.TweetVO;

public interface TweetStorage {
	public void storeTweet(TweetVO tweet) throws TweetStorageException;
}
