package hexlet.code.pages.users;

import hexlet.code.components.SideBar;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.*;

import java.util.List;

public class UserPage extends BasePage {
    private final By emailField = By.xpath("//*[@name='email']");
    private final By firstNameField = By.xpath("//*[@name='firstName']");
    private final By lastNameField = By.xpath("//*[@name='lastName']");

    private final By saveButton = By.xpath("//*[@aria-label='Save']");
    private final By editButton = By.xpath("//*[@aria-label='Edit']");
    private final By deleteButton = By.xpath("//*[@aria-label='Delete']");
    private final By showButton = By.xpath("//*[@aria-label='Show']");
    private final By confirmDeleteButton = By.xpath("//*[@role='dialog']//button[contains(text(), 'Confirm')]");

    private final By successCreatePopup = By.xpath("//*[contains(text(), 'Element created')]");
    private final By successUpdatedPopup = By.xpath("//*[contains(text(), 'Element updated')]");
    private final By successDeletePopup = By.xpath("//*[contains(text(), 'Element deleted')]");
    private final By emailValidationError = By.xpath(
            "//*[contains(@class, 'MuiFormHelperText-root') and contains(@class, 'Mui-error')]"
    );
    private final By requiredValidationError = By.xpath("//*[text()='Required']");

    protected UserPage(WebDriver driver) {
        super(driver);
    }

    public UsersListPage createUserAndReturnToList(User user) {
        createUser(user);
        return new SideBar(driver).getUsersListPage();
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
        waitForElementClickable(editButton).click();
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

    public boolean deleteUser() {
        waitForElementClickable(deleteButton).click();
        waitForElementClickable(confirmDeleteButton).click();
        waitForElementVisible(successDeletePopup);
        return true;
    }

    public String getEmailValue() {
        return waitForElementVisible(emailField).getAttribute("value");
    }

    public String getFirstNameValue() {
        return waitForElementVisible(firstNameField).getAttribute("value");
    }

    public String getLastNameValue() {
        return waitForElementVisible(lastNameField).getAttribute("value");
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
        if (hasBrowserValidationMessage(emailField)) {
            return true;
        }

        try {
            List<org.openqa.selenium.WebElement> errors = driver.findElements(emailValidationError);
            return errors.stream().anyMatch(org.openqa.selenium.WebElement::isDisplayed);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRequiredValidationErrorVisible() {
        if (hasBrowserValidationMessage(emailField)) {
            return true;
        }

        try {
            waitForElementVisible(requiredValidationError);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean hasValidationError() {
        return isEmailValidationErrorVisible() || isRequiredValidationErrorVisible();
    }

    private boolean hasBrowserValidationMessage(By field) {
        WebElement element = waitForElementVisible(field);
        Object message = ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].validationMessage;", element
        );
        return message != null && !message.toString().isBlank();
    }
}
