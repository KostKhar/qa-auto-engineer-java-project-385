package hexlet.code.tests;

import hexlet.code.components.SideBar;
import hexlet.code.data.RandomTestData;
import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.statuses.Status;
import hexlet.code.pages.statuses.StatusPage;
import hexlet.code.pages.statuses.StatusesListPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static hexlet.code.tests.cleanup.CleanupExtension.cleanup;
import static org.junit.jupiter.api.Assertions.*;

class StatusPageTest extends BaseTest {
    private StatusesListPage statusesListPage;

    @BeforeEach
    void login() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        statusesListPage = dashboardPage.getSideBar().getStatusesListPage();
        assertNotNull(statusesListPage);
    }

    private StatusesListPage createStatusOnList(Status status) {
        statusesListPage = statusesListPage.clickCreateStatus().createStatusAndReturnToList(status);
        return statusesListPage;
    }

    @Test
    @DisplayName("Отображение полей формы статуса")
    void checkStatusFormFields() {
        StatusPage statusPage = statusesListPage.clickCreateStatus();

        assertTrue(statusPage.isNameFieldVisible());
        assertTrue(statusPage.isSlugFieldVisible());
        assertTrue(statusPage.isSaveButtonVisible());
    }

    @Test
    @DisplayName("Создание нового статуса")
    void checkCreateNewStatus() {
        Status testStatus = RandomTestData.getStatus();
        cleanup().trackStatus(testStatus.getName());

        createStatusOnList(testStatus);

        Status statusInList = statusesListPage.getStatusByName(testStatus.getName());
        assertEquals(testStatus.getName(), statusInList.getName());
        assertEquals(testStatus.getSlug(), statusInList.getSlug());
    }

    @Test
    @DisplayName("Форма редактирования заполнена данными статуса")
    void checkEditFormPrefilled() {
        Status testStatus = RandomTestData.getStatus();
        cleanup().trackStatus(testStatus.getName());

        createStatusOnList(testStatus);

        StatusPage statusPage = statusesListPage.openStatusByName(testStatus.getName()).openEditForm();

        assertEquals(testStatus.getName(), statusPage.getNameValue());
        assertEquals(testStatus.getSlug(), statusPage.getSlugValue());
    }

    @Test
    @DisplayName("Редактирование данных статуса")
    void checkUpdateStatus() {
        Status testStatus = RandomTestData.getStatus();
        cleanup().trackStatus(testStatus.getName());

        createStatusOnList(testStatus);

        Status updatedStatus = RandomTestData.getStatus();
        cleanup().trackStatus(updatedStatus.getName());

        statusesListPage = statusesListPage.updateStatusByName(testStatus.getName(), updatedStatus);

        Status statusInList = statusesListPage.getStatusByName(updatedStatus.getName());
        assertEquals(updatedStatus.getName(), statusInList.getName());
        assertEquals(updatedStatus.getSlug(), statusInList.getSlug());
        assertTrue(statusesListPage.isStatusNotExists(testStatus.getName()));
    }

    @Test
    @DisplayName("Валидация пустого имени при создании статуса")
    void checkEmptyNameOnCreate() {
        StatusPage statusPage = statusesListPage.clickCreateStatus();
        Status invalidStatus = new Status("", "valid-slug");

        statusPage.fillStatusForm(invalidStatus);
        statusPage.submitFormWithoutWaitingForSuccess();

        assertTrue(statusPage.hasValidationError());

        statusesListPage = new SideBar(driver).getStatusesListPage();
        assertTrue(statusesListPage.isStatusNotExistsBySlug("valid-slug"));
    }

    @Test
    @DisplayName("Валидация пустого имени при обновлении статуса")
    void checkEmptyNameOnUpdate() {
        Status testStatus = RandomTestData.getStatus();
        cleanup().trackStatus(testStatus.getName());

        createStatusOnList(testStatus);

        StatusPage statusPage = statusesListPage.openStatusByName(testStatus.getName()).openEditForm();
        statusPage.fillStatusForm(new Status("", testStatus.getSlug()));
        statusPage.submitFormWithoutWaitingForSuccess();

        assertTrue(statusPage.hasValidationError());

        statusesListPage = new SideBar(driver).getStatusesListPage();
        assertTrue(statusesListPage.isStatusExists(testStatus.getName()));
    }

    @Test
    @DisplayName("Удаление статуса")
    void checkDeleteStatus() {
        Status testStatus = RandomTestData.getStatus();

        createStatusOnList(testStatus);
        assertTrue(statusesListPage.isStatusExists(testStatus.getName()));

        assertTrue(statusesListPage.deleteStatusByName(testStatus.getName()));

        statusesListPage = new SideBar(driver).getStatusesListPage();
        assertTrue(statusesListPage.isStatusNotExists(testStatus.getName()));
    }
}
