package hexlet.code.pages.users;

import hexlet.code.components.Header;
import hexlet.code.components.Table;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class UsersListPage extends BasePage {
    private Header header;
    private final Table<User> usersTable;


    private final By createButton = By.xpath("//*[@aria-label='Create']");
    private final By exportButton = By.xpath("//*[@aria-label='Export']");
    private final By deleteButton = By.xpath("//*[@aria-label='Export']");
    private final By successDeletePopup = By.xpath("//*[@text='Element deleted']");


    public UsersListPage(WebDriver driver) {
        super(driver);

        this.usersTable = new Table<>(driver, usersTableContainer(), row -> {
            List<WebElement> cells = row.findElements(By.xpath(".//td"));
            return new User(
                    row,
                    cells.get(0).getText().trim(),
                    cells.get(1).getText().trim(),
                    cells.get(2).getText().trim(),
                    cells.get(3).getText().trim(),
                    cells.get(4).getText().trim()
            );
        });
    }

    @Override
    protected void initComponents() {
        header = new Header(driver);
    }

    private WebElement usersTableContainer() {
        return driver.findElement(By.className("MuiTable-root RaDatagrid-table css-1dbcj55"));
    }


    public UserPage clickAddUser() {
        waitForElementClickable(createButton);
        driver.findElement(createButton).click();
        return new UserPage(driver);
    }

    public Table<User> getTable() {
        return usersTable;
    }


    public User getUserByEmail(String email) {
        return usersTable.findRowObjectByColumnValue(3, email);
    }

    public boolean deleteUserByEmail(String email) {
        User user = getUserByEmail(email);
        if (user != null) {
            user.clickCheckbox();
            driver.findElement(deleteButton).click();
            return driver.findElement(successDeletePopup).isDisplayed();
        } else {
            throw new RuntimeException("User with email " + email + " not found");
        }
    }

    public String getUserEmail(int rowIndex) {
        return usersTable.getCellText(rowIndex, 2);
    }


    public boolean isUserExists(String email) {
        return usersTable.containsValueInColumn(2, email);
    }

}
