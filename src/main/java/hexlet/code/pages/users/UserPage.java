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
        waiter().waitForCondition(driver -> usersListPage.isUserExists(user.getEmail()));
        return usersListPage;
    }

    public void fillUserForm(User user) {
        openEditForm();
        if (user.getEmail() != null) {
            elementAction().find(emailField).clearAndSendKeys(user.getEmail());
        }
        if (user.getFirstname() != null) {
            elementAction().find(firstNameField).clearAndSendKeys(user.getFirstname());
        }
        if (user.getLastname() != null) {
            elementAction().find(lastNameField).clearAndSendKeys(user.getLastname());
        }
    }

    public void createUser(User user) {
        fillUserForm(user);
        elementAction().find(saveButton).click();
        waiter().waitForVisible(successCreatePopup);
        waiter().waitForInvisible(successCreatePopup);
    }

    public UserPage openEditForm() {
        if (isEditFormOpen()) {
            return this;
        }
        elementAction().find(editButton).click();
        waiter().waitForVisible(emailField);
        return this;
    }

    private boolean isEditFormOpen() {
        try {
            WebElement field = waiter().waitForVisible(emailField);
            return field.isDisplayed() && field.isEnabled() && "input".equalsIgnoreCase(field.getTagName());
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean updateUser(User user) {
        fillUserForm(user);
        elementAction().find(saveButton).click();
        waiter().waitForVisible(successUpdatedPopup);
        waiter().waitForInvisible(successUpdatedPopup);
        return true;
    }

    public void submitFormWithoutWaitingForSuccess() {
        elementAction().find(saveButton).click();
    }

    public String getEmailValue() {
        return elementAction().find(emailField).getAttribute(VALUE_ATTRIBUTE);
    }

    public String getFirstNameValue() {
        return elementAction().find(firstNameField).getAttribute(VALUE_ATTRIBUTE);
    }

    public String getLastNameValue() {
        return elementAction().find(lastNameField).getAttribute(VALUE_ATTRIBUTE);
    }

    public boolean isEmailFieldVisible() {
        return elementAction().find(emailField).isEnabled();
    }

    public boolean isFirstNameFieldVisible() {
        return elementAction().find(firstNameField).isEnabled();
    }

    public boolean isLastNameFieldVisible() {
        return elementAction().find(lastNameField).isEnabled();
    }

    public boolean isSaveButtonVisible() {
        return elementAction().find(saveButton).isEnabled();
    }

    public boolean isUpdateSuccessful() {
        return elementAction().find(successUpdatedPopup).isEnabled();
    }

    public boolean isEmailValidationErrorVisible() {
        return elementAction().hasFieldValidationError(emailField, emailValidationError);
    }

    public boolean isRequiredValidationErrorVisible() {
        return elementAction().hasBrowserValidationMessage(emailField)
                || elementAction().hasVisibleGlobalValidationError(requiredValidationError);
    }

    public boolean hasValidationError() {
        return isEmailValidationErrorVisible() || isRequiredValidationErrorVisible();
    }
}
