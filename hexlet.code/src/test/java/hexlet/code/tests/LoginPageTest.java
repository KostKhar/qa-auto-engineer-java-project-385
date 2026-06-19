package hexlet.code.tests;

import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.LoginPage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginPageTest extends BasePageTest {

    @Test
    void checkLoginByUsernameAndPassword() {
        LoginPage loginPage = new LoginPage(driver);

        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        String expected = dashboardPage.getTitleOfContentRoot();
        String actual = dashboardPage.getTitleOfContentRootByLocator();
        assertEquals(expected, actual, "Title of content root is not equal to " + expected);
    }

    @Test
    void checkLoginWithoutUsername_returnError() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.signInByLoginAndPassword("", "password");

        assertEquals("Required", loginPage.getErrorMessageText());
    }

    @Test
    void checkLoginWithoutPassword_returnError() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.signInByLoginAndPassword("admin", "");

        assertEquals("Required", loginPage.getErrorMessageText());
    }
}
