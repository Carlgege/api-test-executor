package com.jollychic.enums;

public enum TestResult {

    PASS("pass"),
    FAIL("fail"),
    SKIP("skip");

    private final String name;

    TestResult(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
