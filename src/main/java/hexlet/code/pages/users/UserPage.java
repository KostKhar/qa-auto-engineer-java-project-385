package hexlet.code.pages.users;

import hexlet.code.components.SideBar;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class UserPage extends BasePage {
    private final By emailField = By.xpath("//*[@name='email']");
    private final By firstNameField = By.xpath("//*[@name='firstName']");
    private final By lastNameField = By.xpath("//*[@name='lastName']");
    private final By saveButton = By.xpath("//*[@aria-label='Save']");
    private final By editButton = By.xpath("//*[@aria-label='Edit']");

    private static final String VALUE_ATTRIBUTE = "value";

    private final By successCreatePopup = By.xpath(
            "//*[contains(text(), 'Element created') or contains(text(), 'created')]"
    );
    private final By successUpdatedPopup = By.xpath(
            "//*[contains(text(), 'Element updated') or contains(text(), 'updated')]"
    );
    private final By emailValidationError = By.xpath(
            "//*[contains(@class, 'MuiFormHelperText-root') and contains(@class, 'Mui-error')]"
    );
    private final By requiredValidationError = By.xpath("//*[text()='Required']");

    protected UserPage(WebDriver driver) {
        super(driver);
    }

    public UsersListPage createUserAndReturnToList(User user) {
        createUser(user);
        UsersListPage usersListPage = new SideBar(getDriver()).getUsersListPage();
        waitForCondition(driver -> usersListPage.isUserExists(user.getEmail()));
        return usersListPage;
    }

    public void fillUserForm(User user) {
        openEditForm();
        if (user.getEmail() != null) {
            waitForElementClearAndSendKeys(emailField, user.getEmail());
        }
        if (user.getFirstname() != null) {
            waitForElementClearAndSendKeys(firstNameField, user.getFirstname());
        }
        if (user.getLastname() != null) {
            waitForElementClearAndSendKeys(lastNameField, user.getLastname());
        }
    }

    public void createUser(User user) {
        fillUserForm(user);
        clickElement(saveButton);
        waitForElementVisible(successCreatePopup);
        waitForElementInvisible(successCreatePopup);
    }

    public UserPage openEditForm() {
        if (isEditFormOpen()) {
            return this;
        }
        clickElement(editButton);
        waitForElementVisible(emailField);
        return this;
    }

    private boolean isEditFormOpen() {
        try {
            WebElement field = waitForElementVisible(emailField);
            return field.isDisplayed() && field.isEnabled() && "input".equalsIgnoreCase(field.getTagName());
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean updateUser(User user) {
        fillUserForm(user);
        clickElement(saveButton);
        waitForElementVisible(successUpdatedPopup);
        waitForElementInvisible(successUpdatedPopup);
        return true;
    }

    public void submitFormWithoutWaitingForSuccess() {
        clickElement(saveButton);
    }

    public String getEmailValue() {
        return waitForElementVisible(emailField).getAttribute(VALUE_ATTRIBUTE);
    }

    public String getFirstNameValue() {
        return waitForElementVisible(firstNameField).getAttribute(VALUE_ATTRIBUTE);
    }

    public String getLastNameValue() {
        return waitForElementVisible(lastNameField).getAttribute(VALUE_ATTRIBUTE);
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
        return hasFieldValidationError(emailField, emailValidationError);
    }

    public boolean isRequiredValidationErrorVisible() {
        return hasBrowserValidationMessage(emailField)
                || hasVisibleGlobalValidationError(requiredValidationError);
    }

    public boolean hasValidationError() {
        return isEmailValidationErrorVisible() || isRequiredValidationErrorVisible();
    }
}
