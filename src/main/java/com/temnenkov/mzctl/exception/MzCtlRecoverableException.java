package com.temnenkov.mzctl.exception;

public class MzCtlRecoverableException extends RuntimeException {
    public MzCtlRecoverableException(String message, Throwable cause) {
        super(message, cause);
    }
}
