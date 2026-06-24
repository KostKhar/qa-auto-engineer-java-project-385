package hexlet.code.pages.statuses;

import hexlet.code.components.SideBar;
import hexlet.code.components.Table;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

public class StatusesListPage extends BasePage {
    private static final int NAME_COLUMN_INDEX = 2;
    private static final int SLUG_COLUMN_INDEX = 3;

    private final Table<Status> statusesTable;

    private final By createButton = By.xpath("//*[@aria-label='Create']");
    private final By deleteButton = By.xpath("//*[@aria-label='Delete']");
    private final By selectAllCheckbox = By.xpath(
            "//*[contains(@class, 'RaList-main')]//thead//span[contains(@class, 'MuiCheckbox-root')]"
    );
    private final By confirmDeleteButton = By.xpath("//*[@role='dialog']//button[contains(text(), 'Confirm')]");
    private final By successDeletePopup = By.xpath("//*[contains(text(), 'Element deleted')]");
    private final By tableContainer = By.className("RaList-main");

    public StatusesListPage(WebDriver driver) {
        super(driver);

        this.statusesTable = new Table<>(driver, statusesTableContainer(), row -> {
            List<WebElement> cells = row.findElements(By.xpath(".//td"));
            String slug = cells.size() > SLUG_COLUMN_INDEX ? cells.get(SLUG_COLUMN_INDEX).getText().trim() : "";
            return new Status(
                    row,
                    cells.get(1).getText().trim(),
                    cells.get(NAME_COLUMN_INDEX).getText().trim(),
                    slug
            );
        });
    }

    private WebElement statusesTableContainer() {
        return driver.findElement(tableContainer);
    }

    public boolean isTableVisible() {
        waitForElementVisible(tableContainer);
        return true;
    }

    public boolean isTableLoaded() {
        waitForElementVisible(tableContainer);
        return !statusesTable.getRows().isEmpty();
    }

    public boolean hasColumnHeaders(String... expectedHeaders) {
        List<String> headers = statusesTable.getHeaders();
        return Arrays.stream(expectedHeaders)
                .allMatch(expected -> headers.stream()
                        .anyMatch(header -> header.equalsIgnoreCase(expected)));
    }

    public boolean isCreateButtonVisible() {
        return waitForElementVisible(createButton).isDisplayed();
    }

    public StatusPage clickCreateStatus() {
        waitForElementClickable(createButton).click();
        return new StatusPage(driver);
    }

    public StatusPage openStatusByName(String name) {
        Status status = findStatusInTable(name);
        if (status == null) {
            throw new IllegalArgumentException(String.format("Status with name '%s' does not exist", name));
        }
        status.clickStatus();
        return new StatusPage(driver);
    }

    public StatusesListPage updateStatusByName(String name, Status updatedStatus) {
        StatusPage statusPage = openStatusByName(name);
        statusPage.openEditForm();
        statusPage.updateStatus(updatedStatus);
        return new SideBar(driver).getStatusesListPage();
    }

    private Status findStatusInTable(String name) {
        return statusesTable.findRowObjectByColumnValue(NAME_COLUMN_INDEX, name);
    }

    public Status getStatusByName(String name) {
        return findStatusInTable(name);
    }

    public boolean isStatusExists(String name) {
        return statusesTable.containsValueInColumn(NAME_COLUMN_INDEX, name);
    }

    public boolean isStatusNotExists(String name) {
        return statusesTable.findRowObjectByColumnValue(NAME_COLUMN_INDEX, name) == null;
    }

    public boolean isStatusExistsBySlug(String slug) {
        return statusesTable.containsValueInColumn(SLUG_COLUMN_INDEX, slug);
    }

    public boolean isStatusNotExistsBySlug(String slug) {
        return !isStatusExistsBySlug(slug);
    }

    public String getStatusName(int rowIndex) {
        return statusesTable.getCellText(rowIndex, NAME_COLUMN_INDEX);
    }

    public String getStatusSlug(int rowIndex) {
        return statusesTable.getCellText(rowIndex, SLUG_COLUMN_INDEX);
    }

    public Status getStatusAtRow(int rowIndex) {
        return statusesTable.getRowAsObject(rowIndex);
    }

    public boolean isRowContainsKeyFields(int rowIndex) {
        Status status = getStatusAtRow(rowIndex);
        return !status.getName().isBlank() && !status.getSlug().isBlank();
    }

    public boolean deleteStatusByName(String name) {
        Status status = findStatusInTable(name);
        if (status == null) {
            return false;
        }
        status.clickCheckbox();
        deleteSelectedStatuses();
        return true;
    }

    public void selectStatusByName(String name) {
        Status status = findStatusInTable(name);
        if (status == null) {
            throw new IllegalArgumentException(String.format("Status with name '%s' does not exist", name));
        }
        status.clickCheckbox();
    }

    public void selectAllStatuses() {
        waitForElementClickable(selectAllCheckbox).click();
    }

    public void deleteSelectedStatuses() {
        waitForElementClickable(deleteButton).click();
        waitForElementClickable(confirmDeleteButton).click();
        waitForElementVisible(successDeletePopup);
    }

    public int getTableRowCount() {
        return statusesTable.getRows().size();
    }
}
