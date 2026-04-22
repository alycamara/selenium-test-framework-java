package com.orangehrm;

import com.orangehrm.base.BaseClass;
import com.orangehrm.pages.HomePage;
import com.orangehrm.pages.LoginPage;
import com.orangehrm.utilities.DataProviders;
import com.orangehrm.utilities.ExtentManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LoginPageTest extends BaseClass {
    private LoginPage loginPage;
    private HomePage homePage;

    @BeforeMethod
    public void setupPages() {
        loginPage = new LoginPage();
        homePage = new HomePage();
    }

    // =====================================================
    // TEST 1 : LOGIN VALIDE
    // =====================================================

    @Test(dataProvider="validLoginData", dataProviderClass = DataProviders.class, priority = 1)
    public void loginWithValidCredentials(String username, String password) {

        loginPage.login(username, password);

        boolean isAdminTabVisible = homePage.isAdminTabVisible();

        assertTrue(isAdminTabVisible, "Admin tab should be visible after login");

        ExtentManager.logStepWithScreenshot(
                getDriver(),
                "Login successful - Admin tab visible",
                "Valid Login Test Passed"
        );
        homePage.logout();
        ExtentManager.logStep("Logged out Successfully!");
    }

    // =====================================================
    // TEST 2 : LOGIN INVALID
    // =====================================================

    @Test(dataProvider="invalidLoginData", dataProviderClass = DataProviders.class, priority = 2)
    public void loginWithInvalidCredentials(String username, String password) {


        loginPage.login(username, password);

        boolean isErrorDisplayed = loginPage.isErrorMessageDisplayed();

        assertTrue(isErrorDisplayed, "Error message should be displayed");
        String messageError = "Invalid credentials";

        assertTrue(
                loginPage.verifyErrorMessage(
                        messageError),"Error message text should match expected");

        ExtentManager.logStepWithScreenshot(
                getDriver(),
                "Invalid login handled correctly",
                "Invalid Login Test Passed"
        );

    }





}
