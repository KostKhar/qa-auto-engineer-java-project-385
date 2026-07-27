package hexlet.code.tests;

import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DashboardPageTest extends BaseTest {

    @Test
    @DisplayName("Нажатие кнопки выхода из системы")
    void checkClickLogoutButton() {
        LoginPage loginPage = new LoginPage(driver);

        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        LoginPage loginPageAfterLogout = dashboardPage.getHeader().clickLogoutButton();

        assertTrue(loginPageAfterLogout.isLoginElementsIsVisible());
    }
}
