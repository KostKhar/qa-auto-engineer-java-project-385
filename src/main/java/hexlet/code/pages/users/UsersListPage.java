package hexlet.code.pages.users;

import hexlet.code.components.SideBar;
import hexlet.code.components.Table;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UsersListPage extends BasePage {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsersListPage.class);

    private static final int ID_COLUMN_INDEX = 1;
    private static final int EMAIL_COLUMN_INDEX = 2;
    private static final int FIRSTNAME_COLUMN_INDEX = 3;
    private static final int LASTNAME_COLUMN_INDEX = 4;
    private static final int CREATED_AT_COLUMN_INDEX = 5;

    private final By exportButton = By.xpath("//*[@aria-label='Export']");
    private final By searchInput = By.cssSelector("input.RaSearchInput-input");
    private final Table<User> table;

    public UsersListPage(WebDriver driver) {
        super(driver);
        this.table = Table.create(driver, row -> {
            List<WebElement> cells = row.findElements(By.xpath(".//td"));
            return new User(
                    row,
                    cells.get(1).getText().trim(),
                    cells.get(2).getText().trim(),
                    cells.get(FIRSTNAME_COLUMN_INDEX).getText().trim(),
                    cells.get(LASTNAME_COLUMN_INDEX).getText().trim(),
                    cells.get(CREATED_AT_COLUMN_INDEX).getText().trim()
            );
        });
        table.waitForReady();
    }

    public boolean isExportButtonVisible() {
        return elementAction().find(exportButton).isDisplayed();
    }

    public int getUsersCount() {
        return table.getRowCount();
    }

    public boolean isTableEmpty() {
        return getUsersCount() == 0;
    }

    public boolean hasColumnHeaders() {
        return table.hasColumnHeaders("Id", "Email", "First name", "Last name", "Created at");
    }

    public boolean isCreateButtonVisible() {
        return table.isCreateButtonVisible();
    }

    public boolean isTableLoaded() {
        return hasColumnHeaders() && table.isTableLoaded();
    }

    public User getUserAtRow(int rowIndex) {
        return table.getRowAsObject(rowIndex);
    }

    public boolean isRowContainsKeyFields(int rowIndex) {
        User user = getUserAtRow(rowIndex);
        return !user.getEmail().isBlank()
                && !user.getFirstname().isBlank()
                && !user.getLastname().isBlank();
    }

    public UserPage clickCreateUser() {
        table.clickCreateButton();
        return new UserPage(getDriver());
    }

    public UserPage openUserByEmail(String email) {
        User user = findUserInTable(email);
        if (user == null) {
            searchUser(email);
            user = findUserInTable(email);
        }
        if (user == null) {
            throw new IllegalArgumentException(String.format("User with email '%s' does not exist", email));
        }

        user.clickUser();
        return new UserPage(getDriver());
    }

    private User findUserInTable(String email) {
        return table.findRowObjectByColumnValue(EMAIL_COLUMN_INDEX, email);
    }

    public User getUserByEmail(String email) {
        searchUser(email);
        return table.findRowObjectByColumnValue(EMAIL_COLUMN_INDEX, email);
    }

    public UsersListPage updateUserByEmail(String email, User userToUpdate) {
        UserPage userPage = openUserByEmail(email);
        userPage.openEditForm();
        userPage.updateUser(userToUpdate);
        UsersListPage usersListPage = new SideBar(getDriver()).getUsersListPage();
        waiter().waitForCondition(driver -> usersListPage.isUserExists(userToUpdate.getEmail())
                && usersListPage.isUserNotExists(email));
        return usersListPage;
    }

    public boolean deleteUserByEmail(String email) {
        User user = findUserInTable(email);
        if (user == null) {
            searchUser(email);
            user = findUserInTable(email);
        }
        if (user == null) {
            return false;
        }

        user.clickCheckbox();
        deleteSelectedUsers();
        return true;
    }

    public void selectUserByEmail(String email) {
        User user = findUserInTable(email);
        if (user == null) {
            searchUser(email);
            user = findUserInTable(email);
        }
        if (user == null) {
            throw new IllegalArgumentException(String.format("User with email '%s' does not exist", email));
        }
        user.clickCheckbox();
    }

    public void selectAllUsers() {
        table.selectAllRows();
    }

    public void deleteSelectedUsers() {
        table.deleteSelectedRows();
    }

    public String getUserId(int rowIndex) {
        return table.getCellText(rowIndex, ID_COLUMN_INDEX);
    }

    public String getUserEmail(int rowIndex) {
        return table.getCellText(rowIndex, EMAIL_COLUMN_INDEX);
    }

    public String getUserFirstName(int rowIndex) {
        return table.getCellText(rowIndex, FIRSTNAME_COLUMN_INDEX);
    }

    public String getUserLastName(int rowIndex) {
        return table.getCellText(rowIndex, LASTNAME_COLUMN_INDEX);
    }

    public String getUserCreatedAt(int rowIndex) {
        return table.getCellText(rowIndex, CREATED_AT_COLUMN_INDEX);
    }

    public boolean isUserExists(String email) {
        if (table.containsValueInColumn(EMAIL_COLUMN_INDEX, email)) {
            return true;
        }

        searchUser(email);
        return waiter().waitForCondition(driver -> table.containsValueInColumn(EMAIL_COLUMN_INDEX, email));
    }

    public boolean isUserNotExists(String email) {
        if (table.containsValueInColumn(EMAIL_COLUMN_INDEX, email)) {
            return false;
        }

        searchUser(email);
        return findUserInTable(email) == null;
    }

    private void searchUser(String query) {
        try {
            WebElement input = waiter().waitForVisible(searchInput);
            input.clear();
            input.sendKeys(query);
            waiter().waitForPageLoaded();
        } catch (org.openqa.selenium.TimeoutException e) {
            LOGGER.warn("Timed out waiting for user search", e);
        }
    }
}
