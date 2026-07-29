package hexlet.code.tests;

import hexlet.code.components.SideBar;
import hexlet.code.data.RandomTestData;
import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.users.User;
import hexlet.code.pages.users.UsersListPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static hexlet.code.tests.cleanup.CleanupExtension.cleanup;
import static org.junit.jupiter.api.Assertions.*;

class UsersListPageTest extends BaseTest {

    private UsersListPage usersListPage;

    private static Stream<String> seedUserEmails() {
        return RandomTestData.getSeedUserEmails().stream();
    }

    @BeforeEach
    void login() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        this.usersListPage = dashboardPage.getSideBar().getUsersListPage();
        assertNotNull(this.usersListPage, "User list page is null");
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
        assertTrue(usersListPage.hasColumnHeaders());
    }

    @Test
    @DisplayName("Строка таблицы пользователей содержит ключевые поля")
    void checkUsersListRowContainsKeyFields() {
        assertTrue(usersListPage.isRowContainsKeyFields(0));
        assertFalse(usersListPage.getUserId(0).isBlank());
        assertFalse(usersListPage.getUserEmail(0).isBlank());
        assertFalse(usersListPage.getUserFirstName(0).isBlank());
        assertFalse(usersListPage.getUserLastName(0).isBlank());
        assertFalse(usersListPage.getUserCreatedAt(0).isBlank());
    }

    @Test
    @DisplayName("Пользователи загружены в таблице")
    void checkUsersAreLoaded() {
        assertAll(
                () -> assertTrue(usersListPage.isTableLoaded()),
                () -> assertTrue(usersListPage.getUsersCount() > 0),
                () -> assertTrue(usersListPage.isUserExists("alice@hotmail.com")),
                () -> assertTrue(usersListPage.isUserExists("john@google.com")),
                () -> assertTrue(usersListPage.isUserExists("jane@gmail.com")),
                () -> assertTrue(usersListPage.isUserExists("michael@example.com"))
        );
    }

    @DisplayName("Предустановленные пользователи отображаются в таблице")
    @ParameterizedTest
    @MethodSource("seedUserEmails")
    void checkSeedUserExistsInTable(String email) {
        assertTrue(usersListPage.isUserExists(email),
                "seed user '" + email + "' should be visible in table");
    }

    @Test
    @DisplayName("Удаление пользователя из списка через таблицу")
    void checkDeleteUserFromTable() {
        User testUser = RandomTestData.getUser();

        usersListPage = usersListPage.clickCreateUser().createUserAndReturnToList(testUser);
        assertTrue(usersListPage.isUserExists(testUser.getEmail()));

        assertTrue(usersListPage.deleteUserByEmail(testUser.getEmail()));

        usersListPage = new SideBar(driver).getUsersListPage();
        assertTrue(usersListPage.isUserNotExists(testUser.getEmail()));
    }

    @Test
    @DisplayName("Массовое удаление пользователей")
    void checkBulkDeleteUsers() {
        User user1 = RandomTestData.getUser();
        User user2 = RandomTestData.getUser();
        cleanup().trackUser(user1.getEmail());
        cleanup().trackUser(user2.getEmail());

        usersListPage = usersListPage.clickCreateUser().createUserAndReturnToList(user1);
        usersListPage = usersListPage.clickCreateUser().createUserAndReturnToList(user2);

        usersListPage.selectUserByEmail(user1.getEmail());
        usersListPage.selectUserByEmail(user2.getEmail());
        usersListPage.deleteSelectedUsers();

        usersListPage = new SideBar(driver).getUsersListPage();
        assertTrue(usersListPage.isUserNotExists(user1.getEmail()));
        assertTrue(usersListPage.isUserNotExists(user2.getEmail()));
        cleanup().clear();
    }

    @Test
    @DisplayName("Массовое удаление всех пользователей")
    void checkSelectAllAndDeleteAllUsers() {
        List<User> usersBeforeDelete = new ArrayList<>();
        for (int i = 0; i < usersListPage.getUsersCount(); i++) {
            usersBeforeDelete.add(usersListPage.getUserAtRow(i));
        }

        try {
            assertTrue(usersListPage.isTableLoaded());

            usersListPage.selectAllUsers();
            usersListPage.deleteSelectedUsers();

            assertTrue(usersListPage.isTableEmpty());
        } finally {
            restoreUsers(usersBeforeDelete);
        }
    }

    private void restoreUsers(List<User> users) {
        try {
            UsersListPage listPage = new SideBar(driver).getUsersListPage();
            if (!listPage.isTableEmpty() && listPage.isUserExists("alice@hotmail.com")) {
                return;
            }
            for (User user : users) {
                listPage = listPage.clickCreateUser().createUserAndReturnToList(
                        new User(user.getEmail(), user.getFirstname(), user.getLastname())
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
