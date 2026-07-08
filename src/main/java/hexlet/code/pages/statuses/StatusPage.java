package hexlet.code.pages.statuses;

import hexlet.code.components.SideBar;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class StatusPage extends BasePage {
    private final By nameField = By.xpath("//*[@name='name']");
    private final By slugField = By.xpath("//*[@name='slug']");
    private final By saveButton = By.xpath("//*[@aria-label='Save']");
    private final By editButton = By.xpath("//*[@aria-label='Edit']");
    private final By successCreatePopup = By.xpath("//*[contains(text(), 'created')]");
    private final By successUpdatedPopup = By.xpath("//*[contains(text(), 'updated')]");
    private final By validationError = By.xpath(
            "//*[contains(@class, 'MuiFormHelperText-root') and contains(@class, 'Mui-error')]"
    );
    private final By requiredValidationError = By.xpath("//*[text()='Required']");

    public StatusPage(WebDriver driver) {
        super(driver);
    }

    public void fillStatusForm(Status status) {
        waitForElementClearAndSendKeys(nameField, status.getName());
        waitForElementClearAndSendKeys(slugField, status.getSlug());
    }

    public StatusesListPage createStatusAndReturnToList(Status status) {
        fillStatusForm(status);
        waitForElementClickable(saveButton).click();
        waitForElementVisible(successCreatePopup);
        return new SideBar(getDriver()).getStatusesListPage();
    }

    public StatusPage openEditForm() {
        if (isEditFormOpen()) {
            return this;
        }
        waitForElementClickable(editButton).click();
        waitForElementVisible(nameField);
        return this;
    }

    private boolean isEditFormOpen() {
        try {
            WebElement field = waitForElementVisible(nameField);
            return field.isDisplayed() && field.isEnabled() && "input".equalsIgnoreCase(field.getTagName());
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean updateStatus(Status status) {
        fillStatusForm(status);
        waitForElementClickable(saveButton).click();
        return waitForElementVisible(successUpdatedPopup).isDisplayed();
    }

    public void submitFormWithoutWaitingForSuccess() {
        waitForElementClickable(saveButton).click();
    }

    public String getNameValue() {
        return waitForElementVisible(nameField).getAttribute("value");
    }

    public String getSlugValue() {
        return waitForElementVisible(slugField).getAttribute("value");
    }

    public boolean isNameFieldVisible() {
        return waitForElementVisible(nameField).isDisplayed();
    }

    public boolean isSlugFieldVisible() {
        return waitForElementVisible(slugField).isDisplayed();
    }

    public boolean isSaveButtonVisible() {
        return waitForElementVisible(saveButton).isDisplayed();
    }

    public boolean hasValidationError() {
        return isNameValidationErrorVisible()
                || isSlugValidationErrorVisible()
                || isRequiredValidationErrorVisible();
    }

    public boolean isNameValidationErrorVisible() {
        return hasBrowserValidationMessage(nameField) || hasVisibleValidationError(nameField, validationError);
    }

    public boolean isSlugValidationErrorVisible() {
        return hasBrowserValidationMessage(slugField) || hasVisibleValidationError(slugField, validationError);
    }

    public boolean isRequiredValidationErrorVisible() {
        if (hasBrowserValidationMessage(nameField) || hasBrowserValidationMessage(slugField)) {
            return true;
        }
        return hasVisibleGlobalValidationError(requiredValidationError);
    }
}
