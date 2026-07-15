package hexlet.code.tests;

import hexlet.code.components.SideBar;
import hexlet.code.data.RandomTestData;
import hexlet.code.pages.DashboardPage;
import hexlet.code.pages.LoginPage;
import hexlet.code.pages.labels.Label;
import hexlet.code.pages.labels.LabelPage;
import hexlet.code.pages.labels.LabelsListPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LabelPageTest extends BasePageTest {
    private final List<String> namesToCleanup = new ArrayList<>();
    private LabelsListPage labelsListPage;

    @BeforeEach
    void login() {
        LoginPage loginPage = new LoginPage(driver);
        DashboardPage dashboardPage = loginPage.signInByLoginAndPassword("admin", "password");
        labelsListPage = dashboardPage.getSideBar().getLabelsListPage();
        assertNotNull(labelsListPage);
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

    private LabelsListPage createLabelOnList(Label label) {
        labelsListPage = labelsListPage.clickCreateLabel().createLabelAndReturnToList(label);
        return labelsListPage;
    }

    @Test
    @DisplayName("Отображение полей формы метки")
    void checkLabelFormFields() {
        LabelPage labelPage = labelsListPage.clickCreateLabel();

        assertTrue(labelPage.isNameFieldVisible());
        assertTrue(labelPage.isSaveButtonVisible());
    }

    @Test
    @DisplayName("Создание новой метки")
    void checkCreateNewLabel() {
        Label testLabel = RandomTestData.getLabel();
        trackForCleanup(testLabel.getName());

        createLabelOnList(testLabel);

        Label labelInList = labelsListPage.getLabelByName(testLabel.getName());
        assertEquals(testLabel.getName(), labelInList.getName());
    }

    @Test
    @DisplayName("Форма редактирования заполнена данными метки")
    void checkEditFormPrefilled() {
        Label testLabel = RandomTestData.getLabel();
        trackForCleanup(testLabel.getName());

        createLabelOnList(testLabel);

        LabelPage labelPage = labelsListPage.openLabelByName(testLabel.getName()).openEditForm();

        assertEquals(testLabel.getName(), labelPage.getNameValue());
    }

    @Test
    @DisplayName("Редактирование данных метки")
    void checkUpdateLabel() {
        Label testLabel = RandomTestData.getLabel();
        trackForCleanup(testLabel.getName());

        createLabelOnList(testLabel);

        Label updatedLabel = RandomTestData.getLabel();
        trackForCleanup(updatedLabel.getName());

        labelsListPage = labelsListPage.updateLabelByName(testLabel.getName(), updatedLabel);

        Label labelInList = labelsListPage.getLabelByName(updatedLabel.getName());
        assertEquals(updatedLabel.getName(), labelInList.getName());
        assertTrue(labelsListPage.isLabelNotExists(testLabel.getName()));
    }

    @Test
    @DisplayName("Валидация пустого имени при создании метки")
    void checkEmptyNameOnCreate() {
        LabelPage labelPage = labelsListPage.clickCreateLabel();
        Label invalidLabel = new Label("");
        trackForCleanup(invalidLabel.getName());

        labelPage.fillLabelForm(invalidLabel);
        assertTrue(labelPage.isSaveButtonNotClickable());
    }

    @Test
    @DisplayName("Валидация пустого имени при обновлении метки")
    void checkEmptyNameOnUpdate() {
        Label testLabel = RandomTestData.getLabel();
        trackForCleanup(testLabel.getName());

        createLabelOnList(testLabel);

        LabelPage labelPage = labelsListPage.openLabelByName(testLabel.getName()).openEditForm();
        labelPage.fillLabelForm(new Label(""));
        labelPage.submitFormWithoutWaitingForSuccess();

        assertTrue(labelPage.validationErrorIsDisplayed());

        labelsListPage = new SideBar(driver).getLabelsListPage();
        assertTrue(labelsListPage.isLabelExists(testLabel.getName()),
                "label should remain unchanged after failed update");
    }

    @Test
    @DisplayName("Удаление метки")
    void checkDeleteLabel() {
        Label testLabel = RandomTestData.getLabel();

        createLabelOnList(testLabel);
        assertTrue(labelsListPage.isLabelExists(testLabel.getName()));

        LabelPage labelPage = labelsListPage.openLabelByName(testLabel.getName());

        labelPage.deleteLabel();

        labelsListPage = new SideBar(driver).getLabelsListPage();
        assertTrue(labelsListPage.isLabelNotExists(testLabel.getName()));
    }
}
