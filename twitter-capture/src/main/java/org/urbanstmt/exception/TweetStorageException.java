package org.urbanstmt.exception;


@SuppressWarnings("serial")
public class TweetStorageException extends Exception
{

    public TweetStorageException()
    {
        super();
    }

    public TweetStorageException(String message)
    {
        super(message);
    }

    public TweetStorageException(Throwable cause)
    {
        super(cause);
    }

    public TweetStorageException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
