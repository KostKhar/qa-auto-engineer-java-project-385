package hexlet.code.tests;

import hexlet.code.components.SideBar;
import hexlet.code.data.RandomTestData;
import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.statuses.Status;
import hexlet.code.pages.statuses.StatusesListPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static hexlet.code.tests.cleanup.CleanupExtension.cleanup;
import static org.junit.jupiter.api.Assertions.*;

class StatusesListPageTest extends BaseTest {
    private static final List<Status> SEED_STATUSES = List.of(
            new Status("Draft", "draft"),
            new Status("To Review", "to-review"),
            new Status("To Be Fixed", "to-be-fixed"),
            new Status("To Publish", "to-publish"),
            new Status("Published", "published")
    );

    private StatusesListPage statusesListPage;

    @BeforeEach
    void login() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        statusesListPage = dashboardPage.getSideBar().getStatusesListPage();
        assertNotNull(statusesListPage, "Statuses list page is null");
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
        assertAll(
                () -> assertTrue(statusesListPage.isTableVisible()),
                () -> assertTrue(statusesListPage.isRowContainsKeyField(0)),
                () -> assertTrue(statusesListPage.isRowContainsKeyField(1)),
                () -> assertTrue(statusesListPage.isRowContainsKeyField(2)),
                () -> assertTrue(statusesListPage.isRowContainsKeyField(3)),
                () -> assertTrue(statusesListPage.isCreateButtonVisible())
        );
    }

    @Test
    @DisplayName("Статусы загружены в таблице")
    void checkStatusesAreLoaded() {
        assertAll(
                () -> assertTrue(statusesListPage.isTableLoaded()),
                () -> assertTrue(statusesListPage.getStatusesCount() > 0),
                () -> assertTrue(statusesListPage.isStatusExists("Draft")),
                () -> assertTrue(statusesListPage.isStatusExists("To Review")),
                () -> assertTrue(statusesListPage.isStatusExists("To Be Fixed")),
                () -> assertTrue(statusesListPage.isStatusExists("To Publish")),
                () -> assertTrue(statusesListPage.isStatusExists("Published"))
        );
    }

    @DisplayName("Предустановленные статусы отображаются в таблице")
    @ParameterizedTest
    @ValueSource(strings = {"Draft", "To Review", "To Be Fixed", "To Publish", "Published"})
    void checkSeedStatusExistsInTable(String statusName) {
        assertTrue(statusesListPage.isStatusExists(statusName),
                "seed status '" + statusName + "' should be visible in table");
    }

    @Test
    @DisplayName("Удаление статуса из списка через таблицу")
    void checkDeleteStatusFromTable() {
        Status testStatus = RandomTestData.getStatus();

        statusesListPage = statusesListPage.clickCreateStatus().createStatusAndReturnToList(testStatus);
        assertTrue(statusesListPage.isStatusExists(testStatus.getName()));

        assertTrue(statusesListPage.deleteStatusByName(testStatus.getName()));

        statusesListPage = new SideBar(driver).getStatusesListPage();
        assertTrue(statusesListPage.isStatusNotExists(testStatus.getName()));
    }

    @Test
    @DisplayName("Массовое удаление статусов")
    void checkBulkDeleteStatuses() {
        Status status1 = RandomTestData.getStatus();
        Status status2 = RandomTestData.getStatus();
        cleanup().trackStatus(status1.getName());
        cleanup().trackStatus(status2.getName());

        statusesListPage = statusesListPage.clickCreateStatus().createStatusAndReturnToList(status1);
        statusesListPage = statusesListPage.clickCreateStatus().createStatusAndReturnToList(status2);

        statusesListPage.selectStatusByName(status1.getName());
        statusesListPage.selectStatusByName(status2.getName());
        statusesListPage.deleteSelectedStatuses();

        statusesListPage = new SideBar(driver).getStatusesListPage();
        assertTrue(statusesListPage.isStatusNotExists(status1.getName()));
        assertTrue(statusesListPage.isStatusNotExists(status2.getName()));
        cleanup().clear();
    }

    @Test
    @DisplayName("Выбор всех статусов и снятие выделения")
    void checkSelectAllAndDeselectStatuses() {
        Status status1 = RandomTestData.getStatus();
        Status status2 = RandomTestData.getStatus();
        cleanup().trackStatus(status1.getName());
        cleanup().trackStatus(status2.getName());

        statusesListPage = statusesListPage.clickCreateStatus().createStatusAndReturnToList(status1);
        statusesListPage = statusesListPage.clickCreateStatus().createStatusAndReturnToList(status2);

        assertTrue(statusesListPage.isStatusExists(status1.getName()));
        assertTrue(statusesListPage.isStatusExists(status2.getName()));

        statusesListPage.selectAllStatuses();
        statusesListPage.selectAllStatuses();

        assertTrue(statusesListPage.isStatusExists(status1.getName()));
        assertTrue(statusesListPage.isStatusExists(status2.getName()));
    }

    @Test
    @DisplayName("Массовое удаление всех статусов")
    void checkSelectAllAndDeleteAllStatuses() {
        try {
            assertTrue(statusesListPage.isTableLoaded());

            statusesListPage.selectAllStatuses();
            statusesListPage.deleteSelectedStatuses();

            assertTrue(statusesListPage.isTableEmpty());
        } finally {
            restoreSeedStatuses();
        }
    }

    private void restoreSeedStatuses() {
        try {
            StatusesListPage listPage = new SideBar(driver).getStatusesListPage();
            if (!listPage.isTableEmpty() && listPage.isStatusExists("Draft")) {
                return;
            }
            for (Status seed : SEED_STATUSES) {
                listPage = listPage.clickCreateStatus().createStatusAndReturnToList(seed);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
