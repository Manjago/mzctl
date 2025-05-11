package com.temnenkov.mzctl.context;

public enum SimpleContextHolder {
    INSTANCE;

    private final SimpleContext simpleContext;

    SimpleContextHolder() {
        this.simpleContext = new SimpleContext();
    }

    public SimpleContext getSimpleContext() {
        return simpleContext;
    }
}
