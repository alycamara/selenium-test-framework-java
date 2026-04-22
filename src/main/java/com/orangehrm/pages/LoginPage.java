package com.orangehrm.pages;

import com.orangehrm.actiondriver.ActionDriver;
import com.orangehrm.base.BaseClass;
import com.orangehrm.utilities.LoggerManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

public class LoginPage extends BaseClass {
    public static final Logger logger =
            LoggerManager.getLogger(LoginPage.class);

    private ActionDriver actionDriver;

    /**
     * Champ username sur l’interface login.
     */
    private By userNameField = By.name("username");

    /**
     * Champ password sécurisé.
     */
    private By passworField = By.cssSelector("input[type='password']");

    /**
     * Bouton de soumission du formulaire login.
     */
    private By loginButton = By.xpath("//button[text()=' Login ']");

    private By errorMessage = By.xpath("//p[text()='Invalid credentials']");


    public LoginPage() {

        this.actionDriver = BaseClass.getActionDriver();

        logger.info("LoginPage initialized successfully.");
    }

    public void login(String userName, String password) {

        logger.info("Starting login action with user: {}", userName);

        // Saisie username
        actionDriver.enterText(userNameField, userName);
        logger.debug("Username entered.");

        // Saisie password
        actionDriver.enterText(passworField, password);
        logger.debug("Password entered.");

        // Click login
        actionDriver.click(loginButton);
        logger.info("Login button clicked.");
    }

    public boolean isErrorMessageDisplayed() {
        logger.info("Checking if error message is displayed.");

        boolean result = actionDriver.isDisplayed(errorMessage);

        logger.info("Error message visibility result: {}", result);

        return result;
    }

    public boolean verifyErrorMessage(String expectedError) {
        logger.info("Verifying error message text. Expected: {}", expectedError);

        boolean result = actionDriver.compareText(errorMessage, expectedError);

        logger.info("Error message text verification result: {}", result);

        return result;
    }

}
