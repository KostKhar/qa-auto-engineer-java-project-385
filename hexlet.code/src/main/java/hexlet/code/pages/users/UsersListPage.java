package hexlet.code.pages.users;

import hexlet.code.components.Header;
import hexlet.code.components.Table;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class UsersListPage extends BasePage {
    private static final int EMAIL_COLUMN_INDEX = 2;

    private Header header;
    private final Table<User> usersTable;

    private final By createButton = By.xpath("//*[@aria-label='Create']");
    private final By exportButton = By.xpath("//*[@aria-label='Export']");
    private final By deleteButton = By.xpath("//*[@aria-label='Delete']");
    private final By confirmDeleteButton = By.xpath("//*[@role='dialog']//button[contains(text(), 'Confirm')]");
    private final By successDeletePopup = By.xpath("//*[contains(text(), 'Element deleted')]");
    private final By searchInput = By.cssSelector("input.RaSearchInput-input");

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

    private WebElement usersTableContainer() {
        return driver.findElement(By.className("RaList-main"));
    }

    public UserPage clickCreateUser() {
        waitForElementClickable(createButton).click();
        return new UserPage(driver);
    }

    public Table<User> getTable() {
        return usersTable;
    }

    public User getUserByEmail(String email) {
        searchUser(email);
        return usersTable.findRowObjectByColumnValue(EMAIL_COLUMN_INDEX, email);
    }

    public boolean updateUserByEmail(String email, User userToUpdate) {
        searchUser(email);
        User user = usersTable.findRowObjectByColumnValue(EMAIL_COLUMN_INDEX, email);
        if (user == null) {
            throw new IllegalArgumentException(String.format("User with email '%s' does not exist", email));
        }

        user.clickUser();
        UserPage userPage = new UserPage(driver);
        return userPage.updateUser(userToUpdate);
    }

    public boolean deleteUserByEmail(String email) {
        searchUser(email);
        User user = usersTable.findRowObjectByColumnValue(EMAIL_COLUMN_INDEX, email);
        if (user == null) {
            return false;
        }

        user.clickCheckbox();
        waitForElementClickable(deleteButton).click();
        waitForElementClickable(confirmDeleteButton).click();
        waitForElementVisible(successDeletePopup);
        return true;
    }

    public String getUserEmail(int rowIndex) {
        return usersTable.getCellText(rowIndex, EMAIL_COLUMN_INDEX);
    }

    public boolean isUserExists(String email) {
        if (usersTable.containsValueInColumn(EMAIL_COLUMN_INDEX, email)) {
            return true;
        }

        searchUser(email);
        return waitForCondition(driver -> usersTable.containsValueInColumn(EMAIL_COLUMN_INDEX, email));
    }

    private void searchUser(String query) {
        WebElement input = waitForElementVisible(searchInput);
        input.clear();
        input.sendKeys(query);
        waitForPageLoaded();
    }
}
