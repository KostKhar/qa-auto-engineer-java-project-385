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
    private User testUser;

    @BeforeEach
    void login() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        usersListPage = dashboardPage.getSideBar().getUsersListPage();
        assertNotNull(usersListPage);
    }

    @AfterEach
    void cleanupCreatedUser() {
        if (testUser == null) {
            return;
        }

        try {
            UsersListPage listPage = new SideBar(driver).getUsersListPage();
            if (listPage.isUserExists(testUser.getEmail())) {
                listPage.deleteUserByEmail(testUser.getEmail());
            }
        } catch (Exception ignored) {
        } finally {
            testUser = null;
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
        testUser = RandomTestData.getUser();
        UserPage userPage = usersListPage.clickCreateUser();

        usersListPage = userPage.createUserAndReturnToList(testUser);
        assertTrue(usersListPage.isUserExists(testUser.getEmail()));
    }

    @Test
    void checkUpdateUser() {
        testUser = RandomTestData.getUser();
        UserPage userPage = usersListPage.clickCreateUser();

        usersListPage = userPage.createUserAndReturnToList(testUser);
        assertTrue(usersListPage.isUserExists(testUser.getEmail()));

        User updateUser =  RandomTestData.getUser();
        usersListPage.updateUserByEmail(testUser.getEmail(), updateUser);
        assertTrue(usersListPage.isUserExists(updateUser.getEmail()));
    }

    @Test
    void shouldUpdateUserWithoutEmail_returnError() {
        testUser = RandomTestData.getUser();
        UserPage userPage = usersListPage.clickCreateUser();

        usersListPage = userPage.createUserAndReturnToList(testUser);
        assertTrue(usersListPage.isUserExists(testUser.getEmail()));

        User updateUser =  RandomTestData.getUser();
        updateUser.setEmail(null);
        usersListPage.updateUserByEmail(testUser.getEmail(), updateUser);
        assertTrue(usersListPage.isUserExists(updateUser.getEmail()));
    }



}
