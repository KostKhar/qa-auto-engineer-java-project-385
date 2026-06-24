package hexlet.code.tests;

import hexlet.code.components.SideBar;
import hexlet.code.data.RandomTestData;
import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.users.User;
import hexlet.code.pages.users.UsersListPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsersListPageTest extends BasePageTest {

    private final List<String> emailsToCleanup = new ArrayList<>();
    private UsersListPage usersListPage;

    @BeforeEach
    void login() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        this.usersListPage = dashboardPage.getSideBar().getUsersListPage();
        assertNotNull(this.usersListPage, "User list page is null");
    }

    @AfterEach
    void cleanupCreatedUsers() {
        try {
            UsersListPage listPage = new SideBar(driver).getUsersListPage();
            for (String email : emailsToCleanup) {
                if (listPage.isUserExists(email)) {
                    listPage.deleteUserByEmail(email);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to clean up users", e);
        } finally {
            emailsToCleanup.clear();
        }
    }

    @Test
    @DisplayName("Отображение страницы списка пользователей")
    void checkUsersListPage() {
        assertTrue(usersListPage.isTableLoaded());
        assertTrue(usersListPage.isCreateButtonVisible());
        assertTrue(usersListPage.isExportButtonVisible());
    }

    @Test
    @DisplayName("Наличие колонок в таблице пользователей")
    void checkUsersListColumns() {
        assertTrue(usersListPage.hasColumnHeaders("Email", "First name", "Last name"));
    }

    @Test
    @DisplayName("Строка таблицы пользователей содержит ключевые поля")
    void checkUsersListRowContainsKeyFields() {
        assertTrue(usersListPage.isRowContainsKeyFields(0));
        assertFalse(usersListPage.getUserEmail(0).isBlank());
        assertFalse(usersListPage.getUserFirstName(0).isBlank());
        assertFalse(usersListPage.getUserLastName(0).isBlank());
    }

    @Test
    @DisplayName("Массовое удаление пользователей")
    void checkBulkDeleteUsers() {
        User user1 = RandomTestData.getUser();
        User user2 = RandomTestData.getUser();
        emailsToCleanup.add(user1.getEmail());
        emailsToCleanup.add(user2.getEmail());

        usersListPage = usersListPage.clickCreateUser().createUserAndReturnToList(user1);
        usersListPage = usersListPage.clickCreateUser().createUserAndReturnToList(user2);

        usersListPage.selectUserByEmail(user1.getEmail());
        usersListPage.selectUserByEmail(user2.getEmail());
        usersListPage.deleteSelectedUsers();

        usersListPage = new SideBar(driver).getUsersListPage();
        assertTrue(usersListPage.isUserNotExists(user1.getEmail()));
        assertTrue(usersListPage.isUserNotExists(user2.getEmail()));
        emailsToCleanup.clear();
    }
}
