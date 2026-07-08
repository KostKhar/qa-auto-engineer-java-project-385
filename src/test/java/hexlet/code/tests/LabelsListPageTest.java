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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        } catch (Exception e) {
            e.printStackTrace();
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
        assertTrue(labelsListPage.hasColumnHeaders("Id", "Name", "Created at"));
    }

    @Test
    @DisplayName("Метки загружены в таблице")
    void checkLabelsAreLoaded() {
        assertAll(
                () -> assertTrue(labelsListPage.isTableLoaded()),
                () -> assertTrue(labelsListPage.getLabelsCount() > 0),
                () -> assertTrue(labelsListPage.isLabelExists("critical")),
                () -> assertTrue(labelsListPage.isLabelExists("task")),
                () -> assertTrue(labelsListPage.isLabelExists("enhancement")),
                () -> assertTrue(labelsListPage.isLabelExists("bug"))
        );
    }

    @DisplayName("Предустановленные метки отображаются в таблице")
    @ParameterizedTest
    @ValueSource(strings = {"critical", "task", "enhancement", "bug"})
    void checkSeedLabelExistsInTable(String labelName) {
        assertTrue(labelsListPage.isLabelExists(labelName),
                "seed label '" + labelName + "' should be visible in table");
    }

    @Test
    @DisplayName("Предустановленные метки остаются в таблице после создания новой")
    void checkSeedLabelsRemainAfterCreatingNewLabel() {
        Label customLabel = RandomTestData.getLabel();
        trackForCleanup(customLabel.getName());

        labelsListPage = labelsListPage.clickCreateLabel().createLabelAndReturnToList(customLabel);

        assertAll(
                () -> assertTrue(labelsListPage.isLabelExists(customLabel.getName())),
                () -> assertTrue(labelsListPage.isLabelExists("critical")),
                () -> assertTrue(labelsListPage.isLabelExists("task")),
                () -> assertTrue(labelsListPage.isLabelExists("enhancement")),
                () -> assertTrue(labelsListPage.isLabelExists("bug"))
        );
    }

    @Test
    @DisplayName("Строка таблицы меток содержит ключевые поля")
    void checkLabelsListRowContainsKeyFields() {
        assertTrue(labelsListPage.isTableLoaded());
        assertTrue(labelsListPage.isRowContainsKeyFields(0));
    }

    @Test
    @DisplayName("Удаление метки из списка через таблицу")
    void checkDeleteLabelFromTable() {
        Label testLabel = RandomTestData.getLabel();

        labelsListPage = labelsListPage.clickCreateLabel().createLabelAndReturnToList(testLabel);
        assertTrue(labelsListPage.isLabelExists(testLabel.getName()));

        assertTrue(labelsListPage.deleteLabelByName(testLabel.getName()));

        labelsListPage = new SideBar(driver).getLabelsListPage();
        assertTrue(labelsListPage.isLabelNotExists(testLabel.getName()));
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
