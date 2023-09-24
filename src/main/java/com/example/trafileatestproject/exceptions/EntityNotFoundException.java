package com.example.trafileatestproject.exceptions;

public class EntityNotFoundException extends BaseException {
    public EntityNotFoundException(final String code, final String message, final Object... arguments) {
        super(code, message, arguments);
    }
}
