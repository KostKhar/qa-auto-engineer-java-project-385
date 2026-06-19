package hexlet.code.pages.users;

import hexlet.code.components.SideBar;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class UserPage extends BasePage {
    private final By emailField = By.xpath("//*[@name='email']");
    private final By firstNameField = By.xpath("//*[@name='firstName']");
    private final By lastNameField = By.xpath("//*[@name='lastName']");

    private final By saveButton = By.xpath("//*[@aria-label='Save']");
    private final By deleteButton = By.xpath("//*[@aria-label='Delete']");
    private final By showButton = By.xpath("//*[@aria-label='Show']");
    private final By confirmDeleteButton = By.xpath("//*[@role='dialog']//button[contains(text(), 'Confirm')]");

    private final By successCreatePopup = By.xpath("//*[contains(text(), 'Element created')]");
    private final By successUpdatedPopup = By.xpath("//*[contains(text(), 'Element updated')]");
    private final By successDeletePopup = By.xpath("//*[contains(text(), 'Element deleted')]");

    protected UserPage(WebDriver driver) {
        super(driver);
    }

    public UsersListPage createUserAndReturnToList(User user) {
        createUser(user);
        return new SideBar(driver).getUsersListPage();
    }

    public boolean createUser(User user) {
        waitForElementAndSendKeys(emailField, user.getEmail());
        waitForElementAndSendKeys(firstNameField, user.getFirstname());
        waitForElementAndSendKeys(lastNameField, user.getLastname());
        waitForElementClickable(saveButton).click();
        waitForElementVisible(successCreatePopup);
        return true;
    }

    public boolean updateUser(User user) {
        waitForElementAndSendKeys(emailField, user.getEmail());
        waitForElementAndSendKeys(firstNameField, user.getFirstname());
        waitForElementAndSendKeys(lastNameField, user.getLastname());
        waitForElementClickable(saveButton).click();
        return isUpdateSuccessful();
    }

    public boolean deleteUser() {
        waitForElementClickable(deleteButton).click();
        waitForElementClickable(confirmDeleteButton).click();
        waitForElementVisible(successDeletePopup);
        return true;
    }

    public boolean isEmailFieldVisible() {
        return waitForElementVisible(emailField).isDisplayed();
    }

    public boolean isFirstNameFieldVisible() {
        return waitForElementVisible(firstNameField).isDisplayed();
    }

    public boolean isLastNameFieldVisible() {
        return waitForElementVisible(lastNameField).isDisplayed();
    }

    public boolean isSaveButtonVisible() {
        return waitForElementVisible(saveButton).isDisplayed();
    }

    public boolean isDeleteButtonVisible() {
        return waitForElementVisible(deleteButton).isDisplayed();
    }

    public boolean isShowButtonVisible() {
        return waitForElementVisible(showButton).isDisplayed();
    }

    public boolean isUpdateSuccessful() {
        return waitForElementVisible(successUpdatedPopup).isDisplayed();
    }
}
