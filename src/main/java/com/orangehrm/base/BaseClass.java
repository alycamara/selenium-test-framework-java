package com.orangehrm.base;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.utilities.ConfigReader;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;
import java.net.URL;
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
        String browser = prop.getProperty("browser");

        if (browser == null || browser.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Browser is not configured.");
        }

        launchBrowser(browser);
        configureBrowser();

        // Initialize ActionDriver for current thread
        actionDriver.set(new ActionDriver(getDriver()));

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

        boolean seleniumGrid = Boolean.parseBoolean(prop.getProperty("seleniumGrid"));
        String gridURL = prop.getProperty("gridURL");

        try {

            if (seleniumGrid) {
                driver.set(createRemoteDriver(browser, gridURL));
                logger.info("RemoteWebDriver created (Grid)");
            } else {
                driver.set(createLocalDriver(browser));
                logger.info("Local WebDriver created");
            }

            // ExtentManager.registerDriver(getDriver());

        } catch (Exception e) {
            throw new RuntimeException("Failed to launch browser: " + browser, e);
        }
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

    private WebDriver createRemoteDriver(String browser, String gridURL) throws Exception {

        if (browser.equalsIgnoreCase("chrome")) {

            ChromeOptions options = getChromeOptions();
            return new RemoteWebDriver(new URL(gridURL), options);

        } else if (browser.equalsIgnoreCase("firefox")) {

            FirefoxOptions options = getFirefoxOptions();
            return new RemoteWebDriver(new URL(gridURL), options);

        } else if (browser.equalsIgnoreCase("edge")) {

            EdgeOptions options = getEdgeOptions();
            return new RemoteWebDriver(new URL(gridURL), options);
        }

        throw new IllegalArgumentException("Browser Not Supported: " + browser);
    }

    private WebDriver createLocalDriver(String browser) {

        if (browser.equalsIgnoreCase("chrome")) {
            return new ChromeDriver(getChromeOptions());
        }

        if (browser.equalsIgnoreCase("firefox")) {
            return new FirefoxDriver(getFirefoxOptions());
        }

        if (browser.equalsIgnoreCase("edge")) {
            return new EdgeDriver(getEdgeOptions());
        }

        throw new IllegalArgumentException("Browser Not Supported: " + browser);
    }

    private ChromeOptions getChromeOptions() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        return options;
    }

    private FirefoxOptions getFirefoxOptions() {

        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");

        return options;
    }

    private EdgeOptions getEdgeOptions() {

        EdgeOptions options = new EdgeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        return options;
    }

    // =====================================================
    // GET PROPERTIES
    // =====================================================

    public static Properties getProp() {
        return prop;
    }
}