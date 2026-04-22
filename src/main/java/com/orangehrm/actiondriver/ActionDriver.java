package com.orangehrm.actiondriver;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ActionDriver {

    private final WebDriver driver;
    private final WebDriverWait wait;
    public static final Logger logger = LoggerManager.getLogger(ActionDriver.class);

    // =====================================================
    // CONSTRUCTOR
    // =====================================================
    public ActionDriver(WebDriver driver) {
        this.driver = driver;

        int explicitWait = Integer.parseInt(
                BaseClass.getProp().getProperty("explicitWait", "20") // 🔥 increased for Grid
        );

        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));

        logger.info("ActionDriver initialized with wait: " + explicitWait);
    }

    // =====================================================
    // CLICK (GRID ROBUST VERSION)
    // =====================================================
    public void click(By by) {
        try {
            WebElement element = wait.until(
                    ExpectedConditions.elementToBeClickable(by)
            );

            // 🔥 Scroll into view (important for Docker/Grid)
            ((JavascriptExecutor) driver)
                    .executeScript("arguments[0].scrollIntoView({block:'center'});", element);

            // 🔥 Native click + fallback JS click
            try {
                element.click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver)
                        .executeScript("arguments[0].click();", element);
            }

            String msg = "Clicked element: " + by;
            logger.info(msg);
            ExtentManager.logStep(msg);

        } catch (Exception e) {

            String msg = "Unable to click element: " + by;

            logger.error(msg, e);
            ExtentManager.logFailure(driver, msg, msg);

            throw e;
        }
    }

    // =====================================================
    // ENTER TEXT (STABLE VERSION)
    // =====================================================
    public void enterText(By by, String value) {
        try {
            WebElement element = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(by)
            );

            element.clear();
            element.sendKeys(value);

            String msg = "Entered text in: " + by;

            logger.info(msg);
            ExtentManager.logStep(msg);

        } catch (Exception e) {

            String msg = "Unable to enter text in: " + by;

            logger.error(msg, e);
            ExtentManager.logFailure(driver, msg, msg);

            throw e;
        }
    }

    // =====================================================
    // IS DISPLAYED (SAFE VERSION)
    // =====================================================
    public boolean isDisplayed(By by) {
        try {
            boolean displayed = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(by)
            ).isDisplayed();

            String msg = "Element displayed: " + by;

            logger.info(msg);
            ExtentManager.logStep(msg);

            return displayed;

        } catch (Exception e) {

            String msg = "Element NOT displayed: " + by;

            logger.warn(msg);
            ExtentManager.logFailure(driver, msg, msg);

            return false;
        }
    }

    // =====================================================
    // GET TEXT (SAFE VERSION)
    // =====================================================
    public String getText(By by) {
        try {
            String text = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(by)
            ).getText();

            logger.info("Text retrieved from: " + by);

            return text;

        } catch (Exception e) {
            logger.error("Unable to get text: " + by, e);
            return "";
        }
    }

    // =====================================================
    // COMPARE TEXT
    // =====================================================
    public boolean compareText(By by, String expectedText) {
        try {
            String actualText = getText(by);

            boolean result = expectedText.equals(actualText);

            String msg = "Compare Text - Expected: "
                    + expectedText + " | Actual: " + actualText;

            if (result) {
                logger.info("MATCH: " + msg);
                ExtentManager.logStep(msg);
            } else {
                logger.error("MISMATCH: " + msg);
                ExtentManager.logFailure(driver, "Text mismatch", msg);
            }

            return result;

        } catch (Exception e) {
            logger.error("Error comparing text: " + by, e);
            return false;
        }
    }

    // =====================================================
    // ELEMENT DESCRIPTION (SAFE FOR GRID)
    // =====================================================
    private String getElementDescription(By locator) {
        try {
            WebElement element = wait.until(
                    ExpectedConditions.presenceOfElementLocated(locator)
            );

            String text = element.getText();

            return "Element: [" + locator + "]"
                    + (isNotEmpty(text) ? " [text=" + truncate(text, 40) + "]" : "");

        } catch (Exception e) {
            logger.warn("Cannot describe element: " + locator);
            return locator.toString();
        }
    }

    // =====================================================
    // WAIT UTILITIES (REUSABLE - NON BREAKING)
    // =====================================================
    public WebElement waitForVisible(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public WebElement waitForClickable(By by) {
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    public WebElement waitForPresence(By by) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    // =====================================================
    // UTIL METHODS
    // =====================================================
    private boolean isNotEmpty(String value) {
        return value != null && !value.isBlank();
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }
}