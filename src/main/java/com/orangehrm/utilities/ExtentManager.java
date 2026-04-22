package com.orangehrm.utilities;

import java.io.File;
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

            String reportDir =
                    System.getProperty("user.dir")
                            + "/target/ExtentReport";

            String reportPath =
                    reportDir + "/ExtentReport.html";

            createDirectory(reportDir);

            ExtentSparkReporter spark =
                    new ExtentSparkReporter(reportPath);

            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("Automation Report");
            spark.config().setReportName("OrangeHRM Framework");
            spark.config().setTimeStampFormat("dd/MM/yyyy HH:mm:ss");

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
                    "ExtentTest is NOT initialized for this thread.");
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

        try {

            String screenshotDir =
                    System.getProperty("user.dir")
                            + "/target/ExtentReport/screenshots";

            createDirectory(screenshotDir);

            String timestamp =
                    LocalDateTime.now()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

            String fileName =
                    sanitizeFileName(name)
                            + "_"
                            + timestamp
                            + ".png";

            File src =
                    ((TakesScreenshot) driver)
                            .getScreenshotAs(OutputType.FILE);

            File dest =
                    new File(screenshotDir + "/" + fileName);

            FileUtils.copyFile(src, dest);

            // relative path for Extent report
            return "screenshots/" + fileName;

        } catch (Exception e) {

            getTest().warning("Screenshot capture failed: " + e.getMessage());
            return null;
        }
    }

    // =====================================================
    // ATTACH SCREENSHOT
    // =====================================================
    public static void attachScreenshot(
            WebDriver driver,
            String message) {

        try {

            String path =
                    takeScreenshot(
                            driver,
                            getTest().getModel().getName()
                    );

            if (path != null) {

                getTest().info(
                        message,
                        MediaEntityBuilder
                                .createScreenCaptureFromPath(path)
                                .build()
                );
            }

        } catch (Exception e) {

            getTest().fail("Screenshot failed: " + e.getMessage());
        }
    }

    // =====================================================
    // Convert screenshot to Base64 format
    // =====================================================
    public static String convertToBase64(File screenShotFile) {

        try {
            byte[] fileContent =
                    FileUtils.readFileToByteArray(screenShotFile);

            return java.util.Base64
                    .getEncoder()
                    .encodeToString(fileContent);

        } catch (Exception e) {

            return "";
        }
    }

    // =====================================================
    // FLUSH REPORT
    // =====================================================
    public static void endTest() {

        if (extent != null) {
            extent.flush();
        }

        test.remove();
    }

    // =====================================================
    // HELPERS
    // =====================================================
    private static void createDirectory(String path) {

        File folder = new File(path);

        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    private static String sanitizeFileName(String value) {

        return value.replaceAll("[^a-zA-Z0-9-_]", "_");
    }
}