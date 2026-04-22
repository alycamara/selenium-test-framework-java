package com.orangehrm.utilities;

import com.orangehrm.base.BaseClass;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static int maxRetryCount = Integer.parseInt(
            BaseClass.getProp().getProperty("retry.count")); // nombre de retries

    @Override
    public boolean retry(ITestResult result) {

        if (retryCount < maxRetryCount) {
            retryCount++;

            System.out.println("Retrying test: "
                    + result.getName()
                    + " | Attempt: " + retryCount);

            return true; // relancer le test
        }

        return false; // stop retry
    }
}