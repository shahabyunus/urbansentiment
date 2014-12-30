package org.urbanstmt.exception;


@SuppressWarnings("serial")
public class TweetStatusParseException extends Exception
{

    public TweetStatusParseException()
    {
        super();
    }

    public TweetStatusParseException(String message)
    {
        super(message);
    }

    public TweetStatusParseException(Throwable cause)
    {
        super(cause);
    }

    public TweetStatusParseException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
