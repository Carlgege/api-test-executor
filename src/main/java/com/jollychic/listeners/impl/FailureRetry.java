package com.jollychic.listeners.impl;

import lombok.extern.slf4j.Slf4j;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

@Slf4j
public class FailureRetry implements IRetryAnalyzer {

    private int retryCount = 1;
    private static final int MAX_RETRY_COUNT = 3;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            String message = "Retrying for '" + result.getTestClass().getName() + "." +
                    result.getName() + "' " + ", " + retryCount + " times";
            log.debug(message);
            retryCount++;
            return true;
        }
        return false;
    }
}
