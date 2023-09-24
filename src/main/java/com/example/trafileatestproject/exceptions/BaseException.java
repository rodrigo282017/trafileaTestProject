package com.example.trafileatestproject.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = false)
public class BaseException extends RuntimeException {

    private final String code;

    public BaseException(final String code, final String message, final Object... arguments) {
        super(formatMessage(message, arguments));
        this.code = code;
    }

    private static String formatMessage(final String message, final Object... args) {
        String formattedMessage = message;
        for (Object arg : args) {
            formattedMessage = formattedMessage.replaceFirst("\\{(\\w*)\\}", String.valueOf(arg));
        }
        return formattedMessage;
    }
}

