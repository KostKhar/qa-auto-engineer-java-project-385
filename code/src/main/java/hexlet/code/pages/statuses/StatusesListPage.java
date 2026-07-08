package hexlet.code.pages.statuses;

import hexlet.code.components.SideBar;
import hexlet.code.components.Table;
import hexlet.code.pages.AbstractListPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class StatusesListPage extends AbstractListPage<Status> {
    private static final int NAME_COLUMN_INDEX = 2;
    private static final int SLUG_COLUMN_INDEX = 3;

    public StatusesListPage(WebDriver driver) {
        super(driver);
        initTable(createTable(driver));
        waitForElementVisible(CREATE_BUTTON);
    }

    private static Table<Status> createTable(WebDriver driver) {
        return new Table<>(driver, waitForTableContainer(driver), row -> {
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
    }

    public boolean isTableVisible() {
        waitForElementVisible(CREATE_BUTTON);
        return !getDriver().findElements(TABLE_CONTAINER).isEmpty()
                || waitForElementVisible(LIST_ROOT).isDisplayed();
    }

    public int getStatusesCount() {
        return getRowCount();
    }

    public boolean isTableEmpty() {
        return getStatusesCount() == 0;
    }

    public StatusPage clickCreateStatus() {
        waitForElementClickable(CREATE_BUTTON).click();
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
        return new SideBar(getDriver()).getStatusesListPage();
    }

    private Status findStatusInTable(String name) {
        return getTable().findRowObjectByColumnValue(NAME_COLUMN_INDEX, name);
    }

    public Status getStatusByName(String name) {
        return findStatusInTable(name);
    }

    public boolean isStatusExists(String name) {
        return getTable().containsValueInColumn(NAME_COLUMN_INDEX, name);
    }

    public boolean isStatusNotExists(String name) {
        return !isStatusExists(name);
    }

    public boolean isStatusNotExistsBySlug(String slug) {
        return !getTable().containsValueInColumn(SLUG_COLUMN_INDEX, slug);
    }

    public Status getStatusAtRow(int rowIndex) {
        return getTable().getRowAsObject(rowIndex);
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
        selectAllRows();
    }

    public void deleteSelectedStatuses() {
        deleteSelectedRows();
    }
}
