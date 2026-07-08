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

    private String valueAttribute = "value";

    private final By successCreatePopup = By.xpath("//*[contains(text(), 'Element created')]");
    private final By successUpdatedPopup = By.xpath("//*[contains(text(), 'Element updated')]");
    private final By emailValidationError = By.xpath(
            "//*[contains(@class, 'MuiFormHelperText-root') and contains(@class, 'Mui-error')]"
    );
    private final By requiredValidationError = By.xpath("//*[text()='Required']");

    protected UserPage(WebDriver driver) {
        super(driver);
    }

    public UsersListPage createUserAndReturnToList(User user) {
        createUser(user);
        return new SideBar(getDriver()).getUsersListPage();
    }

    public void fillUserForm(User user) {
        waitForElementClearAndSendKeys(emailField, user.getEmail());
        waitForElementClearAndSendKeys(firstNameField, user.getFirstname());
        waitForElementClearAndSendKeys(lastNameField, user.getLastname());
    }

    public boolean createUser(User user) {
        fillUserForm(user);
        waitForElementClickable(saveButton).click();
        waitForElementVisible(successCreatePopup);
        return true;
    }

    public UserPage openEditForm() {
        waitForElementVisible(emailField);
        return this;
    }

    public boolean updateUser(User user) {
        fillUserForm(user);
        waitForElementClickable(saveButton).click();
        return isUpdateSuccessful();
    }

    public void submitFormWithoutWaitingForSuccess() {
        waitForElementClickable(saveButton).click();
    }

    public String getEmailValue() {
        return waitForElementVisible(emailField).getAttribute(valueAttribute);
    }

    public String getFirstNameValue() {
        return waitForElementVisible(firstNameField).getAttribute(valueAttribute);
    }

    public String getLastNameValue() {
        return waitForElementVisible(lastNameField).getAttribute(valueAttribute);
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

    public boolean isUpdateSuccessful() {
        return waitForElementVisible(successUpdatedPopup).isDisplayed();
    }

    public boolean isEmailValidationErrorVisible() {
        return hasBrowserValidationMessage(emailField)
                || hasVisibleValidationError(emailField, emailValidationError);
    }

    public boolean isRequiredValidationErrorVisible() {
        return hasBrowserValidationMessage(emailField)
                || hasVisibleGlobalValidationError(requiredValidationError);
    }

    public boolean hasValidationError() {
        return isEmailValidationErrorVisible() || isRequiredValidationErrorVisible();
    }
}
