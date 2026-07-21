package hexlet.code.pages.statuses;

import hexlet.code.components.SideBar;
import hexlet.code.components.Table;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;

import java.util.List;

public class StatusesListPage extends BasePage {
    private static final int NAME_COLUMN_INDEX = 2;
    private static final int SLUG_COLUMN_INDEX = 3;

    private final Table<Status> table;

    public StatusesListPage(WebDriver driver) {
        super(driver);
        this.table = Table.create(driver, row -> {
            List<WebElement> cells = row.findElements(By.xpath(".//td"));
            String slug;
            if (cells.size() > SLUG_COLUMN_INDEX) {
                slug = cells.get(SLUG_COLUMN_INDEX).getText().trim();
            } else {
                slug = "";
            }
            return new Status(
                    row,
                    cells.get(1).getText().trim(),
                    cells.get(NAME_COLUMN_INDEX).getText().trim(),
                    slug
            );
        });
        table.waitForReady();
    }

    public boolean isTableVisible() {
        return table.isVisible();
    }

    public int getStatusesCount() {
        return table.getRowCount();
    }

    public boolean isTableEmpty() {
        return getStatusesCount() == 0;
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

    public StatusPage clickCreateStatus() {
        table.clickCreateButton();
        return new StatusPage(getDriver());
    }

    public StatusPage openStatusByName(String name) {
        Status status = findStatusInTable(name);
        if (status == null) {
            throw new IllegalArgumentException(String.format("Status with name '%s' does not exist", name));
        }
        status.clickStatus();
        return new StatusPage(getDriver());
    }

    public StatusesListPage updateStatusByName(String name, Status updatedStatus) {
        StatusPage statusPage = openStatusByName(name);
        statusPage.openEditForm();
        statusPage.updateStatus(updatedStatus);
        StatusesListPage statusesListPage = new SideBar(getDriver()).getStatusesListPage();
        waiter().waitForCondition(driver -> statusesListPage.isStatusExists(updatedStatus.getName())
                && statusesListPage.isStatusNotExists(name));
        return statusesListPage;
    }

    private Status findStatusInTable(String name) {
        return table.findRowObjectByColumnValue(NAME_COLUMN_INDEX, name);
    }

    private Status waitForStatusInTable(String name) {
        try {
            waiter().waitForCondition(driver -> findStatusInTable(name) != null);
        } catch (TimeoutException e) {
            return null;
        }
        return findStatusInTable(name);
    }

    public Status getStatusByName(String name) {
        return waitForStatusInTable(name);
    }

    public boolean isStatusExists(String name) {
        return table.containsValueInColumn(NAME_COLUMN_INDEX, name);
    }

    public boolean isStatusNotExists(String name) {
        return !isStatusExists(name);
    }

    public boolean isStatusNotExistsBySlug(String slug) {
        return !table.containsValueInColumn(SLUG_COLUMN_INDEX, slug);
    }

    public Status getStatusAtRow(int rowIndex) {
        return table.getRowAsObject(rowIndex);
    }

    public boolean isRowContainsKeyField(int rowIndex) {
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
        table.selectAllRows();
    }

    public void deleteSelectedStatuses() {
        table.deleteSelectedRows();
    }
}
