package com.orangehrm.base;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utilities.ConfigReader;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Properties;

public class BaseClass {

    // =====================================================
    // GLOBAL CONFIGURATION
    // =====================================================

    protected static Properties prop;

    // =====================================================
    // THREAD SAFE OBJECTS
    // =====================================================

    private static final ThreadLocal<WebDriver> driver =
            new ThreadLocal<>();

    private static final ThreadLocal<ActionDriver> actionDriver =
            new ThreadLocal<>();

    // =====================================================
    // LOGGER
    // =====================================================

    public static final Logger logger =
            LoggerManager.getLogger(BaseClass.class);

    // =====================================================
    // BEFORE SUITE
    // =====================================================

    @BeforeSuite
    public void setupSuite() {

        logger.info("Loading configuration file...");

        prop = ConfigReader.initProp();

        if (prop == null) {
            throw new RuntimeException(
                    "Configuration file could not be loaded.");
        }

       // ExtentManager.getReporter(); --This has been implemented in TestListener

        logger.info("Framework initialized successfully.");
    }

    // =====================================================
    // BEFORE EACH TEST
    // =====================================================

    @BeforeMethod
    public void setup(Method method) {

        String testName = method.getName();

        logger.info("Starting test: {}", testName);

        // Start Extent Test
        ExtentManager.startTest(testName);

        String browser = prop.getProperty("browser");

        if (browser == null || browser.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Browser is not configured.");
        }

        launchBrowser(browser);
        configureBrowser();

        // Initialize ActionDriver for current thread
        actionDriver.set(new ActionDriver(getDriver()));


        logger.info("Test setup completed: {}", testName);
    }

    // =====================================================
    // AFTER EACH TEST
    // =====================================================


    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        try {
            if (driver.get() != null) {
                driver.get().quit();
            }
        } catch (Exception e) {
            logger.error("Error closing driver", e);
        } finally {
            driver.remove();
            actionDriver.remove();
        }
    }


    // =====================================================
    // BROWSER LAUNCH
    // =====================================================

    private void launchBrowser(String browser) {

        switch (browser.toLowerCase()) {

            case "chrome":
                driver.set(new ChromeDriver());
                logger.info("Chrome browser launched.");
                break;

            case "firefox":
                driver.set(new FirefoxDriver());
                logger.info("Firefox browser launched.");
                break;

            case "edge":
                driver.set(new EdgeDriver());
                logger.info("Edge browser launched.");
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported browser: " + browser);
        }

        // Register driver for screenshots
       // ExtentManager.registerDriver(getDriver());
    }

    // =====================================================
    // BROWSER CONFIGURATION
    // =====================================================

    private void configureBrowser() {

        int implicitWait = Integer.parseInt(
                prop.getProperty("implicitWait"));

        String url = prop.getProperty("url");

        /*getDriver().manage()
                .timeouts()
                .implicitlyWait(Duration.ofSeconds(implicitWait));*/

        getDriver().manage().window().maximize();

        getDriver().get(url);

        logger.info("Navigated to URL: {}", url);
    }

    // =====================================================
    // GET DRIVER
    // =====================================================

    public static WebDriver getDriver() {

        WebDriver currentDriver = driver.get();

        if (currentDriver == null) {

            logger.error("WebDriver is not initialized.");

            throw new IllegalStateException(
                    "WebDriver is not initialized.");
        }

        return currentDriver;
    }

    // =====================================================
    // GET ACTION DRIVER
    // =====================================================

    public static ActionDriver getActionDriver() {

        ActionDriver currentActionDriver =
                actionDriver.get();

        if (currentActionDriver == null) {

            logger.error("ActionDriver is not initialized.");

            throw new IllegalStateException(
                    "ActionDriver is not initialized.");
        }

        return currentActionDriver;
    }

    // =====================================================
    // GET PROPERTIES
    // =====================================================

    public static Properties getProp() {
        return prop;
    }
}