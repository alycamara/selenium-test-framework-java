package com.orangehrm.pages;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.LoggerManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

public class HomePage {

    public static final Logger logger =
            LoggerManager.getLogger(HomePage.class);

    private ActionDriver actionDriver;

    private By adminTab = By.xpath("//span[text()='Admin']");

    private By userIDButton = By.className("oxd-userdropdown-name");
    private By logoutButton = By.xpath("//a[contains(.,'Logout')]");

    private By oranageHRMlogo = By.xpath("//div[@class='oxd-brand-banner']//img");


    public HomePage() {
        this.actionDriver = BaseClass.getActionDriver();

        logger.info("HomePage initialized successfully.");
    }

    public boolean isAdminTabVisible() {

        logger.info("Checking visibility of Admin tab.");

        boolean result = actionDriver.isDisplayed(adminTab);

        logger.info("Admin tab visibility result: {}", result);

        return result;
    }

    public boolean verifyOrangeHRMlogo() {
        return actionDriver.isDisplayed(oranageHRMlogo);
    }

    // Method to perform logout operation
    public void logout() {

        actionDriver.click(userIDButton);


        actionDriver.waitForVisible(logoutButton);
        actionDriver.waitForClickable(logoutButton);

        actionDriver.click(logoutButton);
    }




}
