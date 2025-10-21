package co.com.foodcourt.usecase.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationMessages {

    ORDER_READY("Your order #%d is ready! PIN: %s.%nThank you for your purchase.");

    private final String template;

    public String format(Object... args) {
        return String.format(template, args);
    }
}
