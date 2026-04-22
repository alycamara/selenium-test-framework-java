package com.orangehrm.listeners;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.RetryAnalyzer;
import org.openqa.selenium.WebDriver;
import org.testng.IAnnotationTransformer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class TestListener implements ITestListener, IAnnotationTransformer {


    // =====================================================
    // APPLY RETRY AUTOMATICALLY
    // =====================================================
    @Override
    public void transform(ITestAnnotation annotation,
                          Class testClass,
                          Constructor testConstructor,
                          Method testMethod) {

        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }

    // =====================================================
    // TEST START
    // =====================================================
    @Override
    public void onTestStart(ITestResult result) {

        String testName = result.getMethod().getMethodName();

        try {
            ExtentManager.startTest(testName);
            ExtentManager.logStep("TEST STARTED: " + testName);
        } catch (Exception e) {
            System.out.println("Extent startTest failed: " + e.getMessage());
        }
    }

    // =====================================================
    // TEST SUCCESS
    // =====================================================
    @Override
    public void onTestSuccess(ITestResult result) {

        String testName = result.getMethod().getMethodName();

        try {
            ExtentManager.logStep("TEST PASSED: " + testName);

            WebDriver driver = safeGetDriver();

            if (driver != null) {
                ExtentManager.logStepWithScreenshot(
                        driver,
                        "Test executed successfully",
                        "SUCCESS: " + testName
                );
            } else {
                ExtentManager.logStep("Test passed (no driver available for screenshot)");
            }

        } catch (Exception e) {
            System.out.println("Listener success error: " + e.getMessage());
        }
    }

    // =====================================================
    // TEST FAILURE
    // =====================================================
    @Override
    public void onTestFailure(ITestResult result) {

        String testName = result.getMethod().getMethodName();

        try {
            Throwable error = result.getThrowable();

            WebDriver driver = safeGetDriver();

            String message = error != null ? error.getMessage() : "Unknown error";

            if (driver != null) {
                ExtentManager.logFailure(
                        driver,
                        "TEST FAILED: " + testName,
                        message
                );
            } else {
                ExtentManager.logStep("TEST FAILED: " + testName + " (no driver available for screenshot)");
            }

        } catch (Exception e) {
            System.out.println("Listener failure error: " + e.getMessage());
        }
    }

    // =====================================================
    // TEST SKIPPED
    // =====================================================
    @Override
    public void onTestSkipped(ITestResult result) {

        String testName = result.getMethod().getMethodName();

        try {
            ExtentManager.logSkip("TEST SKIPPED: " + testName);
        } catch (Exception e) {
            System.out.println("Listener skip error: " + e.getMessage());
        }
    }

    // =====================================================
    // SAFE DRIVER ACCESS
    // =====================================================
    private WebDriver safeGetDriver() {
        try {
            return BaseClass.getDriver();
        } catch (Exception e) {
            return null;
        }
    }

    // Triggered when a suite Starts
    @Override
    public void onStart(ITestContext context) {
        // Initialize the Extent Reports
        ExtentManager.getReporter();
    }

    // Triggered when the suite ends
    @Override
    public void onFinish(ITestContext context) {
        // Flush the Extent Reports
        ExtentManager.endTest();

    }
}