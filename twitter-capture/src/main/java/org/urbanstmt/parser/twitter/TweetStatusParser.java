package org.urbanstmt.parser.twitter;

import org.urbanstmt.exception.TweetStatusParseException;
import org.urbanstmt.model.twitter.TweetVO;

import twitter4j.Status;

public interface TweetStatusParser {
	public TweetVO parse(Status status) throws TweetStatusParseException;
}
