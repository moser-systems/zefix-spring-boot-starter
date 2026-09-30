package com.mosersystems.zefix;

import com.mosersystems.zefix.model.ErrorDetails;
import com.mosersystems.zefix.model.ErrorType;
import org.springframework.http.HttpStatusCode;

/**
 * Thrown when the Zefix API responds with an error status.
 */
public class ZefixApiException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final transient ErrorDetails errorDetails;

    /**
     * @param statusCode   HTTP status of the response
     * @param errorDetails error details from the response body, or {@code null} if the body could not be parsed
     */
    public ZefixApiException(HttpStatusCode statusCode, ErrorDetails errorDetails) {
        super(message(statusCode, errorDetails));
        this.statusCode = statusCode;
        this.errorDetails = errorDetails;
    }

    /**
     * @return HTTP status of the response
     */
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    /**
     * @return error details from the response body, or {@code null} if not available
     */
    public ErrorDetails getErrorDetails() {
        return errorDetails;
    }

    /**
     * @return the error type from the response body, or {@code null} if not available
     */
    public ErrorType getErrorType() {
        return errorDetails != null ? errorDetails.type() : null;
    }

    private static String message(HttpStatusCode statusCode, ErrorDetails errorDetails) {
        String message = "Zefix API error " + statusCode.value();
        if (errorDetails != null) {
            message += " " + errorDetails.type();
            if (errorDetails.message() != null) {
                message += ": " + errorDetails.message();
            }
        }
        return message;
    }
}
