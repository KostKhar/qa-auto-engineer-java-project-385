package hexlet.code.pages.labels;

import hexlet.code.components.SideBar;
import hexlet.code.components.Table;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;

import java.util.List;

public class LabelsListPage extends BasePage {
    private static final int NAME_COLUMN_INDEX = 2;
    private static final int CREATED_AT_COLUMN_INDEX = 3;

    private final Table<Label> table;

    public LabelsListPage(WebDriver driver) {
        super(driver);
        this.table = Table.createForList(driver, row -> {
            List<WebElement> cells = row.findElements(By.xpath(".//td"));
            String createdAt;
            if (cells.size() > CREATED_AT_COLUMN_INDEX) {
                createdAt = cells.get(CREATED_AT_COLUMN_INDEX).getText().trim();
            } else {
                createdAt = "";
            }
            return new Label(
                    row,
                    cells.get(1).getText().trim(),
                    cells.get(NAME_COLUMN_INDEX).getText().trim(),
                    createdAt
            );
        });
    }

    public boolean isTableVisible() {
        return table.isTableContainerVisible();
    }

    public int getLabelsCount() {
        return table.getRowCount();
    }

    public boolean hasColumnHeaders(String... expectedHeaders) {
        return table.hasColumnHeaders(expectedHeaders);
    }

    public boolean isCreateButtonVisible() {
        return table.isCreateButtonVisible();
    }

    public boolean isTableLoaded() {
        return table.isTableLoaded();
    }

    public LabelPage clickCreateLabel() {
        table.clickCreateButton();
        return new LabelPage(getDriver());
    }

    public LabelPage openLabelByName(String name) {
        Label label = waitForLabelInTable(name);
        if (label == null) {
            throw new IllegalArgumentException(String.format("Label with name '%s' does not exist", name));
        }
        label.clickLabel();
        return new LabelPage(getDriver());
    }

    public LabelsListPage updateLabelByName(String name, Label updatedLabel) {
        LabelPage labelPage = openLabelByName(name);
        labelPage.openEditForm();
        labelPage.updateLabel(updatedLabel);
        LabelsListPage labelsListPage = new SideBar(getDriver()).getLabelsListPage();
        waiter().waitForCondition(driver -> labelsListPage.isLabelExists(updatedLabel.getName())
                && labelsListPage.isLabelNotExists(name));
        return labelsListPage;
    }

    private Label findLabelInTable(String name) {
        return table.findRowObjectByColumnValue(NAME_COLUMN_INDEX, name);
    }

    private Label waitForLabelInTable(String name) {
        try {
            waiter().waitForCondition(driver -> findLabelInTable(name) != null);
        } catch (TimeoutException e) {
            return null;
        }
        return findLabelInTable(name);
    }

    public Label getLabelByName(String name) {
        return waitForLabelInTable(name);
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
        table.selectAllRows();
    }

    public void deleteSelectedLabels() {
        table.deleteSelectedRows();
    }
}
