package com.temnenkov.mzctl.di;

// simple DIE - ха-ха!
public sealed class SimpleDIException extends RuntimeException {

    public SimpleDIException(String message) {
        super(message);
    }

    public SimpleDIException(String message, Throwable cause) {
        super(message, cause);
    }

    public static final class BeanInstanceException extends SimpleDIException {
        public BeanInstanceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    public static final class ConstructorNotFoundException extends SimpleDIException {
        public ConstructorNotFoundException(String message) {
            super(message);
        }
    }
}
