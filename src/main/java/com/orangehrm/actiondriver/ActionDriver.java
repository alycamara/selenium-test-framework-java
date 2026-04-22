package com.orangehrm.actiondriver;

import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.ExtentManager;
import com.orangehrm.utilities.LoggerManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
                BaseClass.getProp().getProperty("explicitWait", "10")
        );

        this.wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));

        logger.info("ActionDriver initialized with wait: " + explicitWait);
    }

    // =====================================================
    // CLICK
    // =====================================================
    public void click(By by) {
        try {
            WebElement element = wait.until(
                    ExpectedConditions.elementToBeClickable(by)
            );

            element.click();

            String msg = "Clicked element: " + getElementDescription(by);

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
    // ENTER TEXT
    // =====================================================
    public void enterText(By by, String value) {
        try {
            WebElement element = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(by)
            );

            element.clear();
            element.sendKeys(value);

            String msg = "Entered text in: " + getElementDescription(by);

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
    // IS DISPLAYED
    // =====================================================
    public boolean isDisplayed(By by) {
        try {
            boolean displayed = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(by)
            ).isDisplayed();

            String msg = "Element displayed: " + getElementDescription(by);

            logger.info(msg);
            ExtentManager.logStep(msg);

            return displayed;

        } catch (Exception e) {

            String msg = "Element NOT displayed: " + by;

            logger.error(msg, e);
            ExtentManager.logFailure(driver, msg, msg);

            return false;
        }
    }

    // =====================================================
    // GET TEXT
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
    // ELEMENT DESCRIPTION
    // =====================================================
    private String getElementDescription(By locator) {

        try {
            WebElement element = driver.findElement(locator);

            String id = element.getDomProperty("id");
            String name = element.getDomProperty("name");
            String className = element.getDomProperty("class");
            String placeholder = element.getDomProperty("placeholder");
            String text = element.getText();

            StringBuilder desc = new StringBuilder("Element: ");

            if (isNotEmpty(id)) desc.append("[id=").append(id).append("] ");
            if (isNotEmpty(name)) desc.append("[name=").append(name).append("] ");
            if (isNotEmpty(className)) desc.append("[class=").append(className).append("] ");
            if (isNotEmpty(placeholder)) desc.append("[placeholder=").append(placeholder).append("] ");
            if (isNotEmpty(text)) desc.append("[text=").append(truncate(text, 40)).append("] ");

            if (desc.toString().equals("Element: ")) {
                desc.append(locator.toString());
            }

            return desc.toString();

        } catch (Exception e) {
            logger.error("Error describing element: " + locator, e);
            return locator.toString();
        }
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