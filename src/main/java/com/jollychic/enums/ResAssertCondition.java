package com.jollychic.enums;

public enum ResAssertCondition {

    IS("is"),
    NOT("not");

    private final String name;

    ResAssertCondition(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
