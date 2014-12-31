package org.urbanstmt.analytics.exception;


@SuppressWarnings("serial")
public class AnalyticsServiceBadRequestException extends Exception
{

    public AnalyticsServiceBadRequestException()
    {
        super();
    }

    public AnalyticsServiceBadRequestException(String message)
    {
        super(message);
    }

    public AnalyticsServiceBadRequestException(Throwable cause)
    {
        super(cause);
    }

    public AnalyticsServiceBadRequestException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
