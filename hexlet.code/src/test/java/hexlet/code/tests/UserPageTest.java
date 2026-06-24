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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserPageTest extends BasePageTest {
    private final List<String> emailsToCleanup = new ArrayList<>();
    private UsersListPage usersListPage;

    @BeforeEach
    void login() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        usersListPage = dashboardPage.getSideBar().getUsersListPage();
        assertNotNull(usersListPage);
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
        } catch (Exception ignored) {
        } finally {
            emailsToCleanup.clear();
        }
    }

    private void trackForCleanup(String email) {
        if (email != null && !email.isBlank()) {
            emailsToCleanup.add(email);
        }
    }

    private UsersListPage createUserOnList(User user) {
        usersListPage = usersListPage.clickCreateUser().createUserAndReturnToList(user);
        return usersListPage;
    }

    @Test
    @DisplayName("Отображение формы создания пользователя")
    void checkUserPage() {
        UserPage userPage = usersListPage.clickCreateUser();

        assertTrue(userPage.isEmailFieldVisible());
        assertTrue(userPage.isFirstNameFieldVisible());
        assertTrue(userPage.isLastNameFieldVisible());
        assertTrue(userPage.isSaveButtonVisible());
    }

    @Test
    @DisplayName("Создание нового пользователя")
    void checkCreateNewUser() {
        User testUser = RandomTestData.getUser();
        trackForCleanup(testUser.getEmail());

        createUserOnList(testUser);
        assertTrue(usersListPage.isUserExists(testUser.getEmail()));
    }

    @Test
    @DisplayName("Форма редактирования заполнена данными пользователя")
    void checkEditFormPrefilled() {
        User testUser = RandomTestData.getUser();
        trackForCleanup(testUser.getEmail());

        createUserOnList(testUser);

        UserPage userPage = usersListPage.openUserByEmail(testUser.getEmail()).openEditForm();

        assertEquals(testUser.getEmail(), userPage.getEmailValue());
        assertEquals(testUser.getFirstname(), userPage.getFirstNameValue());
        assertEquals(testUser.getLastname(), userPage.getLastNameValue());
    }

    @Test
    @DisplayName("Редактирование данных пользователя")
    void checkUpdateUser() {
        User testUser = RandomTestData.getUser();
        trackForCleanup(testUser.getEmail());

        createUserOnList(testUser);

        User updatedUser = RandomTestData.getUser();
        trackForCleanup(updatedUser.getEmail());

        usersListPage = usersListPage.updateUserByEmail(testUser.getEmail(), updatedUser);

        User userInList = usersListPage.getUserByEmail(updatedUser.getEmail());
        assertEquals(updatedUser.getEmail(), userInList.getEmail());
        assertEquals(updatedUser.getFirstname(), userInList.getFirstname());
        assertEquals(updatedUser.getLastname(), userInList.getLastname());
        assertTrue(usersListPage.isUserNotExists(testUser.getEmail()));
    }

    @Test
    @DisplayName("Валидация некорректного email при создании пользователя")
    void checkInvalidEmailOnCreate() {
        UserPage userPage = usersListPage.clickCreateUser();
        User invalidUser = new User("invalid-email", "Test", "User");

        userPage.fillUserForm(invalidUser);
        userPage.submitFormWithoutWaitingForSuccess();

        assertTrue(userPage.hasValidationError());

        usersListPage = new SideBar(driver).getUsersListPage();
        assertTrue(usersListPage.isUserNotExists("invalid-email"));
    }

    @Test
    @DisplayName("Валидация некорректного email при обновлении пользователя")
    void checkInvalidEmailOnUpdate() {
        User testUser = RandomTestData.getUser();
        trackForCleanup(testUser.getEmail());

        createUserOnList(testUser);

        UserPage userPage = usersListPage.openUserByEmail(testUser.getEmail()).openEditForm();
        userPage.fillUserForm(new User("invalid-email", testUser.getFirstname(), testUser.getLastname()));
        userPage.submitFormWithoutWaitingForSuccess();

        assertTrue(userPage.hasValidationError());

        usersListPage = new SideBar(driver).getUsersListPage();
        assertTrue(usersListPage.isUserExists(testUser.getEmail()));
    }

    @Test
    @DisplayName("Удаление пользователя")
    void checkDeleteUser() {
        User testUser = RandomTestData.getUser();

        createUserOnList(testUser);
        assertTrue(usersListPage.isUserExists(testUser.getEmail()));

        assertTrue(usersListPage.deleteUserByEmail(testUser.getEmail()));

        usersListPage = new SideBar(driver).getUsersListPage();
        assertTrue(usersListPage.isUserNotExists(testUser.getEmail()));
    }
}
