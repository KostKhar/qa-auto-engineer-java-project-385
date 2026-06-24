package hexlet.code.tests;

import hexlet.code.components.SideBar;
import hexlet.code.data.RandomTestData;
import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.statuses.Status;
import hexlet.code.pages.statuses.StatusesListPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusesListPageTest extends BasePageTest {
    private final List<String> namesToCleanup = new ArrayList<>();
    private StatusesListPage statusesListPage;

    @BeforeEach
    void login() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        statusesListPage = dashboardPage.getSideBar().getStatusesListPage();
        assertNotNull(statusesListPage, "Statuses list page is null");
    }

    @AfterEach
    void cleanupCreatedStatuses() {
        try {
            StatusesListPage listPage = new SideBar(driver).getStatusesListPage();
            for (String name : namesToCleanup) {
                if (listPage.isStatusExists(name)) {
                    listPage.deleteStatusByName(name);
                }
            }
        } catch (Exception ignored) {
        } finally {
            namesToCleanup.clear();
        }
    }

    private void trackForCleanup(String name) {
        if (name != null && !name.isBlank()) {
            namesToCleanup.add(name);
        }
    }

    @Test
    @DisplayName("Отображение страницы списка статусов")
    void checkStatusesListPage() {
        assertTrue(statusesListPage.isTableVisible());
        assertTrue(statusesListPage.isCreateButtonVisible());
    }

    @Test
    @DisplayName("Наличие колонок в таблице статусов")
    void checkStatusesListColumns() {
        assertTrue(statusesListPage.hasColumnHeaders("Name", "Slug"));
    }

    @Test
    @DisplayName("Строка таблицы статусов содержит ключевые поля")
    void checkStatusesListRowContainsKeyFields() {
        assertTrue(statusesListPage.isTableLoaded());
        assertTrue(statusesListPage.isRowContainsKeyFields(0));
    }

    @Test
    @DisplayName("Массовое удаление статусов")
    void checkBulkDeleteStatuses() {
        Status status1 = RandomTestData.getStatus();
        Status status2 = RandomTestData.getStatus();
        trackForCleanup(status1.getName());
        trackForCleanup(status2.getName());

        statusesListPage = statusesListPage.clickCreateStatus().createStatusAndReturnToList(status1);
        statusesListPage = statusesListPage.clickCreateStatus().createStatusAndReturnToList(status2);

        statusesListPage.selectStatusByName(status1.getName());
        statusesListPage.selectStatusByName(status2.getName());
        statusesListPage.deleteSelectedStatuses();

        statusesListPage = new SideBar(driver).getStatusesListPage();
        assertTrue(statusesListPage.isStatusNotExists(status1.getName()));
        assertTrue(statusesListPage.isStatusNotExists(status2.getName()));
        namesToCleanup.clear();
    }

    @Test
    @DisplayName("Выбор всех статусов и снятие выделения")
    void checkSelectAllAndDeselectStatuses() {
        Status status1 = RandomTestData.getStatus();
        Status status2 = RandomTestData.getStatus();
        trackForCleanup(status1.getName());
        trackForCleanup(status2.getName());

        statusesListPage = statusesListPage.clickCreateStatus().createStatusAndReturnToList(status1);
        statusesListPage = statusesListPage.clickCreateStatus().createStatusAndReturnToList(status2);

        assertTrue(statusesListPage.isStatusExists(status1.getName()));
        assertTrue(statusesListPage.isStatusExists(status2.getName()));

        statusesListPage.selectAllStatuses();
        statusesListPage.selectAllStatuses();

        assertTrue(statusesListPage.isStatusExists(status1.getName()));
        assertTrue(statusesListPage.isStatusExists(status2.getName()));
    }
}
