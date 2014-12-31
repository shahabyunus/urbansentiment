package org.urbanstmt.exception;


@SuppressWarnings("serial")
public class AnalyticsStoreException extends Exception
{

    public AnalyticsStoreException()
    {
        super();
    }

    public AnalyticsStoreException(String message)
    {
        super(message);
    }

    public AnalyticsStoreException(Throwable cause)
    {
        super(cause);
    }

    public AnalyticsStoreException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
