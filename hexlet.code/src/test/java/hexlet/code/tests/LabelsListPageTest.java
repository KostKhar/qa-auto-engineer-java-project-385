package hexlet.code.tests;

import hexlet.code.components.SideBar;
import hexlet.code.data.RandomTestData;
import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.labels.Label;
import hexlet.code.pages.labels.LabelsListPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LabelsListPageTest extends BasePageTest {
    private final List<String> namesToCleanup = new ArrayList<>();
    private LabelsListPage labelsListPage;

    @BeforeEach
    void login() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        labelsListPage = dashboardPage.getSideBar().getLabelsListPage();
        assertNotNull(labelsListPage, "Labels list page is null");
    }

    @AfterEach
    void cleanupCreatedLabels() {
        try {
            LabelsListPage listPage = new SideBar(driver).getLabelsListPage();
            for (String name : namesToCleanup) {
                if (listPage.isLabelExists(name)) {
                    listPage.deleteLabelByName(name);
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
    @DisplayName("Отображение страницы списка меток")
    void checkLabelsListPage() {
        assertTrue(labelsListPage.isTableVisible());
        assertTrue(labelsListPage.isCreateButtonVisible());
    }

    @Test
    @DisplayName("Наличие колонок в таблице меток")
    void checkLabelsListColumns() {
        assertTrue(labelsListPage.hasColumnHeaders("Name", "Slug"));
    }

    @Test
    @DisplayName("Строка таблицы меток содержит ключевые поля")
    void checkLabelsListRowContainsKeyFields() {
        assertTrue(labelsListPage.isTableLoaded());
        assertTrue(labelsListPage.isRowContainsKeyFields(0));
    }

    @Test
    @DisplayName("Массовое удаление меток")
    void checkBulkDeleteLabels() {
        Label label1 = RandomTestData.getLabel();
        Label label2 = RandomTestData.getLabel();
        trackForCleanup(label1.getName());
        trackForCleanup(label2.getName());

        labelsListPage = labelsListPage.clickCreateLabel().createLabelAndReturnToList(label1);
        labelsListPage = labelsListPage.clickCreateLabel().createLabelAndReturnToList(label2);

        labelsListPage.selectLabelByName(label1.getName());
        labelsListPage.selectLabelByName(label2.getName());
        labelsListPage.deleteSelectedLabels();

        labelsListPage = new SideBar(driver).getLabelsListPage();
        assertTrue(labelsListPage.isLabelNotExists(label1.getName()));
        assertTrue(labelsListPage.isLabelNotExists(label2.getName()));
        namesToCleanup.clear();
    }

    @Test
    @DisplayName("Выбор всех меток и снятие выделения")
    void checkSelectAllAndDeselectLabels() {
        Label label1 = RandomTestData.getLabel();
        Label label2 = RandomTestData.getLabel();
        trackForCleanup(label1.getName());
        trackForCleanup(label2.getName());

        labelsListPage = labelsListPage.clickCreateLabel().createLabelAndReturnToList(label1);
        labelsListPage = labelsListPage.clickCreateLabel().createLabelAndReturnToList(label2);

        assertTrue(labelsListPage.isLabelExists(label1.getName()));
        assertTrue(labelsListPage.isLabelExists(label2.getName()));

        labelsListPage.selectAllLabels();
        labelsListPage.selectAllLabels();

        assertTrue(labelsListPage.isLabelExists(label1.getName()));
        assertTrue(labelsListPage.isLabelExists(label2.getName()));
    }
}
