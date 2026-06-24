package hexlet.code.pages.users;

import hexlet.code.components.Header;
import hexlet.code.components.SideBar;
import hexlet.code.components.Table;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

public class UsersListPage extends BasePage {
    private static final int EMAIL_COLUMN_INDEX = 2;
    private static final int FIRSTNAME_COLUMN_INDEX = 3;
    private static final int LASTNAME_COLUMN_INDEX = 4;
    private final Table<User> usersTable;
    private final By createButton = By.xpath("//*[@aria-label='Create']");
    private final By exportButton = By.xpath("//*[@aria-label='Export']");
    private final By deleteButton = By.xpath("//*[@aria-label='Delete']");
    private final By selectAllCheckbox = By.xpath("//thead//input[@type='checkbox']");
    private final By confirmDeleteButton = By.xpath("//*[@role='dialog']//button[contains(text(), 'Confirm')]");
    private final By successDeletePopup = By.xpath("//*[contains(text(), 'Element deleted')]");
    private final By tableContainer = By.className("RaList-main");
    private final By searchInput = By.cssSelector("input.RaSearchInput-input");
    private Header header;

    public UsersListPage(WebDriver driver) {
        super(driver);
        initComponents();

        this.usersTable = new Table<>(driver, usersTableContainer(), row -> {
            List<WebElement> cells = row.findElements(By.xpath(".//td"));
            return new User(
                    row,
                    cells.get(1).getText().trim(),
                    cells.get(2).getText().trim(),
                    cells.get(3).getText().trim(),
                    cells.get(4).getText().trim(),
                    cells.get(5).getText().trim()
            );
        });
    }

    @Override
    protected void initComponents() {
        header = new Header(driver);
    }

    public Header getHeader() {
        return header;
    }

    public boolean isCreateButtonVisible() {
        return waitForElementVisible(createButton).isDisplayed();
    }

    public boolean isExportButtonVisible() {
        return waitForElementVisible(exportButton).isDisplayed();
    }

    public boolean isTableLoaded() {
        waitForElementVisible(tableContainer);
        return !usersTable.getRows().isEmpty();
    }

    public boolean hasColumnHeaders(String... expectedHeaders) {
        List<String> headers = usersTable.getHeaders();
        return Arrays.stream(expectedHeaders)
                .allMatch(expected -> headers.stream()
                        .anyMatch(header -> header.equalsIgnoreCase(expected)));
    }

    public User getUserAtRow(int rowIndex) {
        return usersTable.getRowAsObject(rowIndex);
    }

    public boolean isRowContainsKeyFields(int rowIndex) {
        User user = getUserAtRow(rowIndex);
        return !user.getEmail().isBlank()
                && !user.getFirstname().isBlank()
                && !user.getLastname().isBlank();
    }

    private WebElement usersTableContainer() {
        return driver.findElement(tableContainer);
    }

    public UserPage clickCreateUser() {
        waitForElementClickable(createButton).click();
        return new UserPage(driver);
    }

    public Table<User> getTable() {
        return usersTable;
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
        return new UserPage(driver);
    }

    private User findUserInTable(String email) {
        return usersTable.findRowObjectByColumnValue(EMAIL_COLUMN_INDEX, email);
    }

    public User getUserByEmail(String email) {
        searchUser(email);
        return usersTable.findRowObjectByColumnValue(EMAIL_COLUMN_INDEX, email);
    }

    public UsersListPage updateUserByEmail(String email, User userToUpdate) {
        UserPage userPage = openUserByEmail(email);
        userPage.openEditForm();
        userPage.updateUser(userToUpdate);
        return new SideBar(driver).getUsersListPage();
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
        waitForElementClickable(selectAllCheckbox).click();
    }

    public void deleteSelectedUsers() {
        waitForElementClickable(deleteButton).click();
        waitForElementClickable(confirmDeleteButton).click();
        waitForElementVisible(successDeletePopup);
    }

    public void searchByPrefix(String prefix) {
        searchUser(prefix);
    }

    public void clearSearch() {
        WebElement input = waitForElementVisible(searchInput);
        input.clear();
        waitForPageLoaded();
    }

    public String getUserEmail(int rowIndex) {
        return usersTable.getCellText(rowIndex, EMAIL_COLUMN_INDEX);
    }

    public String getUserFirstName(int rowIndex) {
        return usersTable.getCellText(rowIndex, FIRSTNAME_COLUMN_INDEX);
    }

    public String getUserLastName(int rowIndex) {
        return usersTable.getCellText(rowIndex, LASTNAME_COLUMN_INDEX);
    }

    public boolean isUserExists(String email) {
        if (usersTable.containsValueInColumn(EMAIL_COLUMN_INDEX, email)) {
            return true;
        }

        searchUser(email);
        return waitForCondition(driver -> usersTable.containsValueInColumn(EMAIL_COLUMN_INDEX, email));
    }

    public boolean isUserNotExists(String email) {
        if (usersTable.containsValueInColumn(EMAIL_COLUMN_INDEX, email)) {
            return false;
        }

        searchUser(email);

        return findUserInTable(email) == null;
    }

    private void searchUser(String query) {
        try {
            WebElement input = waitForElementVisible(searchInput);
            input.clear();
            input.sendKeys(query);
            waitForPageLoaded();
        } catch (org.openqa.selenium.TimeoutException e) {
            // search input is not available on this page — skip filtering
        }
    }
}
