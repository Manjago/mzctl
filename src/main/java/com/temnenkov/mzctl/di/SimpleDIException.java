package com.temnenkov.mzctl.di;

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
    public static final class BeanNotFoundException extends SimpleDIException {
        public BeanNotFoundException(String message) {
            super(message);
        }
    }
}
