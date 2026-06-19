package hexlet.code.tests;

import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.users.UsersListPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsersListPageTest extends BasePageTest {

    private UsersListPage usersListPage;

    @BeforeEach
    void login() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        this.usersListPage = dashboardPage.getSideBar().getUsersListPage();
        assertNotNull(this.usersListPage, "User list page is null");
    }

    @Test
    void checkUsersListPage() {
        assertFalse(usersListPage.getTable().getTableData().isEmpty());
        assertTrue(usersListPage.isCreateButtonVisible());
        assertTrue(usersListPage.isExportButtonVisible());
    }
}
