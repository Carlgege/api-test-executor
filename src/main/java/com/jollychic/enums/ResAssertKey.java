package com.jollychic.enums;

public enum ResAssertKey {

    MESSAGE("message"),
    MESSAGECODE("messageCode");

    private final String name;

    ResAssertKey(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
