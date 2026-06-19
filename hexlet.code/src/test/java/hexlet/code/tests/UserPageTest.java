package hexlet.code.tests;

import hexlet.code.components.SideBar;
import hexlet.code.data.RandomTestData;
import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.users.User;
import hexlet.code.pages.users.UserPage;
import hexlet.code.pages.users.UsersListPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserPageTest extends BasePageTest {
    private UsersListPage usersListPage;
    private User createdUser;

    @BeforeEach
    void login() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        usersListPage = dashboardPage.getSideBar().getUsersListPage();
        assertNotNull(usersListPage);
    }

    @AfterEach
    void cleanupCreatedUser() {
        if (createdUser == null) {
            return;
        }

        try {
            UsersListPage listPage = new SideBar(driver).getUsersListPage();
            if (listPage.isUserExists(createdUser.getEmail())) {
                listPage.deleteUserByEmail(createdUser.getEmail());
            }
        } catch (Exception ignored) {
        } finally {
            createdUser = null;
        }
    }

    @Test
    void checkUserPage() {
        UserPage userPage = usersListPage.clickCreateUser();

        assertTrue(userPage.isEmailFieldVisible());
        assertTrue(userPage.isFirstNameFieldVisible());
        assertTrue(userPage.isLastNameFieldVisible());
        assertTrue(userPage.isSaveButtonVisible());
    }

    @Test
    void checkCreateNewUser() {
        createdUser = RandomTestData.getUser();
        UserPage userPage = usersListPage.clickCreateUser();

        usersListPage = userPage.createUserAndReturnToList(createdUser);
        assertTrue(usersListPage.isUserExists(createdUser.getEmail()));
    }
}
