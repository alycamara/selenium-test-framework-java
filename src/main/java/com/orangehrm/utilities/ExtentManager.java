package com.orangehrm.utilities;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    // =====================================================
    // INIT REPORT
    // =====================================================
    public static synchronized ExtentReports getReporter() {

        if (extent == null) {

            String reportPath =
                    System.getProperty("user.dir")
                            + "/src/test/resources/ExtentReport/ExtentReport.html";

            ExtentSparkReporter spark =
                    new ExtentSparkReporter(reportPath);

            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("Automation Report");
            spark.config().setReportName("OrangeHRM Framework");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("User", System.getProperty("user.name"));
            extent.setSystemInfo("Java", System.getProperty("java.version"));
        }

        return extent;
    }

    // =====================================================
    // START TEST
    // =====================================================
    public static void startTest(String testName) {
        ExtentTest extentTest =
                getReporter().createTest(testName);

        test.set(extentTest);
    }

    // =====================================================
    // GET TEST SAFE
    // =====================================================
    public static ExtentTest getTest() {

        ExtentTest t = test.get();

        if (t == null) {
            throw new IllegalStateException(
                    "ExtentTest is NOT initialized for this thread. " +
                            "Ensure startTest() is called before logging.");
        }

        return t;
    }

    // =====================================================
    // LOG STEPS
    // =====================================================
    public static void logStep(String message) {
        getTest().info(message);
    }

    public static void logStepWithScreenshot(
            WebDriver driver,
            String message,
            String screenshotName) {

        getTest().pass(message);
        attachScreenshot(driver, screenshotName);
    }

    public static void logFailure(
            WebDriver driver,
            String message,
            String screenshotName) {

        getTest().fail(message);
        attachScreenshot(driver, screenshotName);
    }

    public static void logSkip(String message) {
        getTest().skip(message);
    }

    // =====================================================
    // SCREENSHOT
    // =====================================================
    public static String takeScreenshot(
            WebDriver driver,
            String name) {

        TakesScreenshot ts = (TakesScreenshot) driver;
        File src = ts.getScreenshotAs(OutputType.FILE);

        String timestamp =
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        String path = System.getProperty("user.dir")
                + "/src/test/resources/screenshots/"
                + name + "_" + timestamp + ".png";

        File dest = new File(path);

        try {
            FileUtils.copyFile(src, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return src.getAbsolutePath();
    }

    // =====================================================
    // ATTACH SCREENSHOT
    // =====================================================
    public static void attachScreenshot(
            WebDriver driver,
            String message) {

        try {
            String path = takeScreenshot(driver, getTest().getModel().getName());

            getTest().info(
                    message,
                    MediaEntityBuilder
                            .createScreenCaptureFromPath(path)
                            .build()
            );

        } catch (Exception e) {
            getTest().fail("Screenshot failed: " + e.getMessage());
        }
    }

    // =====================================================
    // FLUSH REPORT
    // =====================================================
    public static void endTest() {

        if (extent != null) {
            extent.flush();
        }
    }
}