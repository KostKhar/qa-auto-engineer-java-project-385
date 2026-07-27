package hexlet.code.tests;

import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static hexlet.code.pages.DashboardPage.TITLE_OF_CONTENT_ROOT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginPageTest extends BaseTest {

    @Test
    @DisplayName("Вход по логину и паролю")
    void checkLoginByUsernameAndPassword() {
        LoginPage loginPage = new LoginPage(driver);

        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        String expected = TITLE_OF_CONTENT_ROOT;
        String actual = dashboardPage.getTitleOfContentRootByLocator();
        assertEquals(expected, actual, "Title of content root is not equal to " + expected);
    }

    @Test
    @DisplayName("Вход без логина возвращает ошибку")
    void checkLoginWithoutUsername_returnError() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.signInByLoginAndPassword("", "password");

        assertEquals("Required", loginPage.getErrorMessageText());
    }

    @Test
    @DisplayName("Вход без пароля возвращает ошибку")
    void checkLoginWithoutPassword_returnError() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.signInByLoginAndPassword("admin", "");

        assertEquals("Required", loginPage.getErrorMessageText());
    }
}
