package com.kankaBot.kankaBot.Exceptions;

public class ConstraintException extends RuntimeException {
    private static final long serialVersionUID = 5723952907135446546L;

    public ConstraintException() {}

    public ConstraintException(String message) {
        super(message);
    }
}
