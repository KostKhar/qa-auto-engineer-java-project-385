package hexlet.code.pages.labels;

import hexlet.code.components.SideBar;
import hexlet.code.components.Table;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

public class LabelsListPage extends BasePage {
    private static final int NAME_COLUMN_INDEX = 2;
    private static final int SLUG_COLUMN_INDEX = 3;

    private final Table<Label> labelsTable;

    private final By createButton = By.xpath("//*[@aria-label='Create']");
    private final By deleteButton = By.xpath("//*[@aria-label='Delete']");
    private final By selectAllCheckbox = By.xpath(
            "//*[contains(@class, 'RaList-main')]//thead//span[contains(@class, 'MuiCheckbox-root')]"
    );
    private final By confirmDeleteButton = By.xpath("//*[@role='dialog']//button[contains(text(), 'Confirm')]");
    private final By successDeletePopup = By.xpath("//*[contains(text(), 'Element deleted')]");
    private final By tableContainer = By.className("RaList-main");

    public LabelsListPage(WebDriver driver) {
        super(driver);

        this.labelsTable = new Table<>(driver, labelsTableContainer(), row -> {
            List<WebElement> cells = row.findElements(By.xpath(".//td"));
            String slug = cells.size() > SLUG_COLUMN_INDEX ? cells.get(SLUG_COLUMN_INDEX).getText().trim() : "";
            return new Label(
                    row,
                    cells.get(1).getText().trim(),
                    cells.get(NAME_COLUMN_INDEX).getText().trim(),
                    slug
            );
        });
    }

    private WebElement labelsTableContainer() {
        return driver.findElement(tableContainer);
    }

    public boolean isTableVisible() {
        waitForElementVisible(tableContainer);
        return true;
    }

    public boolean isTableLoaded() {
        waitForElementVisible(tableContainer);
        return !labelsTable.getRows().isEmpty();
    }

    public boolean hasColumnHeaders(String... expectedHeaders) {
        List<String> headers = labelsTable.getHeaders();
        return Arrays.stream(expectedHeaders)
                .allMatch(expected -> headers.stream()
                        .anyMatch(header -> header.equalsIgnoreCase(expected)));
    }

    public boolean isCreateButtonVisible() {
        return waitForElementVisible(createButton).isDisplayed();
    }

    public LabelPage clickCreateLabel() {
        waitForElementClickable(createButton).click();
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
        labelPage.openEditForm();
        labelPage.updateLabel(updatedLabel);
        return new SideBar(driver).getLabelsListPage();
    }

    private Label findLabelInTable(String name) {
        return labelsTable.findRowObjectByColumnValue(NAME_COLUMN_INDEX, name);
    }

    public Label getLabelByName(String name) {
        return findLabelInTable(name);
    }

    public boolean isLabelExists(String name) {
        return labelsTable.containsValueInColumn(NAME_COLUMN_INDEX, name);
    }

    public boolean isLabelNotExists(String name) {
        return labelsTable.findRowObjectByColumnValue(NAME_COLUMN_INDEX, name) == null;
    }

    public boolean isLabelExistsBySlug(String slug) {
        return labelsTable.containsValueInColumn(SLUG_COLUMN_INDEX, slug);
    }

    public boolean isLabelNotExistsBySlug(String slug) {
        return !isLabelExistsBySlug(slug);
    }

    public String getLabelName(int rowIndex) {
        return labelsTable.getCellText(rowIndex, NAME_COLUMN_INDEX);
    }

    public String getLabelSlug(int rowIndex) {
        return labelsTable.getCellText(rowIndex, SLUG_COLUMN_INDEX);
    }

    public Label getLabelAtRow(int rowIndex) {
        return labelsTable.getRowAsObject(rowIndex);
    }

    public boolean isRowContainsKeyFields(int rowIndex) {
        Label label = getLabelAtRow(rowIndex);
        return !label.getName().isBlank() && !label.getSlug().isBlank();
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
        waitForElementClickable(selectAllCheckbox).click();
    }

    public void deleteSelectedLabels() {
        waitForElementClickable(deleteButton).click();
        waitForElementClickable(confirmDeleteButton).click();
        waitForElementVisible(successDeletePopup);
    }

    public int getTableRowCount() {
        return labelsTable.getRows().size();
    }
}
