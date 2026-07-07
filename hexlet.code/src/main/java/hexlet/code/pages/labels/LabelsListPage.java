package hexlet.code.pages.labels;

import hexlet.code.components.SideBar;
import hexlet.code.components.Table;
import hexlet.code.pages.AbstractListPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class LabelsListPage extends AbstractListPage<Label> {
    private static final int NAME_COLUMN_INDEX = 2;
    private static final int CREATED_AT_COLUMN_INDEX = 3;

    public LabelsListPage(WebDriver driver) {
        super(driver);
        initTable(new Table<>(driver, waitForListTableContainer(driver), row -> {
            List<WebElement> cells = row.findElements(By.xpath(".//td"));
            String createdAt = cells.size() > CREATED_AT_COLUMN_INDEX
                    ? cells.get(CREATED_AT_COLUMN_INDEX).getText().trim()
                    : "";
            return new Label(
                    row,
                    cells.get(1).getText().trim(),
                    cells.get(NAME_COLUMN_INDEX).getText().trim(),
                    createdAt
            );
        }));
    }

    public boolean isTableVisible() {
        waitForElementVisible(TABLE_CONTAINER);
        return true;
    }

    public int getLabelsCount() {
        return getRowCount();
    }

    public LabelPage clickCreateLabel() {
        waitForElementClickable(CREATE_BUTTON).click();
        return new LabelPage(driver);
    }

    public LabelPage openLabelByName(String name) {
        Label label = findLabelInTable(name);
        if (label == null) {
            throw new IllegalArgumentException(String.format("Label with name '%s' does not exist", name));
        }
        label.clickLabel();
        return new LabelPage(driver);
    }

    public LabelsListPage updateLabelByName(String name, Label updatedLabel) {
        LabelPage labelPage = openLabelByName(name);
        labelPage.updateLabel(updatedLabel);
        return new SideBar(driver).getLabelsListPage();
    }

    private Label findLabelInTable(String name) {
        return table.findRowObjectByColumnValue(NAME_COLUMN_INDEX, name);
    }

    public Label getLabelByName(String name) {
        return findLabelInTable(name);
    }

    public boolean isLabelExists(String name) {
        return table.containsValueInColumn(NAME_COLUMN_INDEX, name);
    }

    public boolean isLabelNotExists(String name) {
        return !isLabelExists(name);
    }

    public boolean isRowContainsKeyFields(int rowIndex) {
        Label label = table.getRowAsObject(rowIndex);
        return !label.getName().isBlank() && !label.getCreatedAt().isBlank();
    }

    public boolean deleteLabelByName(String name) {
        Label label = findLabelInTable(name);
        if (label == null) {
            return false;
        }
        label.clickCheckbox();
        deleteSelectedLabels();
        return true;
    }

    public void selectLabelByName(String name) {
        Label label = findLabelInTable(name);
        if (label == null) {
            throw new IllegalArgumentException(String.format("Label with name '%s' does not exist", name));
        }
        label.clickCheckbox();
    }

    public void selectAllLabels() {
        selectAllRows();
    }

    public void deleteSelectedLabels() {
        deleteSelectedRows();
    }
}
