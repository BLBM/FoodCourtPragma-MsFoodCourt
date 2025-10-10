package co.com.foodcourt.consumer.common;

import lombok.Getter;

@Getter
public enum LogConstants {

    ERROR_GET_USER_BY_ID("Error in GetUserById. Cause : {}"),
    ERROR_FALLBACK_EXECUTE("Fallback execute by getUserById. Cause : {}");



    private final String message;

    LogConstants(final String message) {
        this.message = message;
    }
}
