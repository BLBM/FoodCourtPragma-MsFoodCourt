package co.com.foodcourt.consumer.common;

import lombok.Getter;

@Getter
public enum LogConstants {

    ERROR_GET_USER_BY_ID("Error in GetUserById. Cause : {}"),
    ERROR_FALLBACK_EXECUTE("Fallback execute by getUserById. Cause : {}"),
    REQUEST_MS_AUTH("start getUserById :{}"),
    SUCCESS_MS_AUTH("success getUserById :{}"),
    ERROR_MS_AUTH("Error consuming external service"),
    SEND_REQUEST_TRACEABILITY("Sending traceability to MS Traceability: {}"),
    ERROR_MS_TRACEABILITY("Error saving traceability. Code: {}"),
    SUCCESS_MS_TRACEABILITY("Traceability successfully sent to MS Traceability."),
    IO_EXCEPTION_MESSAGE("Unsuccessful service response:"),
    ERROR_EXTERNAL_EXCEPTION("Error communicating with MS Traceability");







    private final String message;

    LogConstants(final String message) {
        this.message = message;
    }
}
