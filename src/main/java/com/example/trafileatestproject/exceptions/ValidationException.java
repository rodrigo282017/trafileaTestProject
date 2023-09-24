package com.example.trafileatestproject.exceptions;

public class ValidationException extends BaseException {
    public ValidationException(final String code, final String message, final Object... arguments) {
        super(code, message, arguments);
    }
}
