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
        if (status.getName() != null) {
            waitForElementClearAndSendKeys(nameField, status.getName());
        }
        if (status.getSlug() != null) {
            waitForElementClearAndSendKeys(slugField, status.getSlug());
        }
    }

    public StatusesListPage createStatusAndReturnToList(Status status) {
        fillStatusForm(status);
        clickElement(saveButton);
        waitForElementVisible(successCreatePopup);
        waitForElementInvisible(successCreatePopup);
        StatusesListPage statusesListPage = new SideBar(getDriver()).getStatusesListPage();
        waitForCondition(driver -> statusesListPage.isStatusExists(status.getName()));
        return statusesListPage;
    }

    public StatusPage openEditForm() {
        if (isEditFormOpen()) {
            return this;
        }
        clickElement(editButton);
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
        clickElement(saveButton);
        waitForElementVisible(successUpdatedPopup);
        waitForElementInvisible(successUpdatedPopup);
        return true;
    }

    public void submitFormWithoutWaitingForSuccess() {
        clickElement(saveButton);
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
        return hasFieldValidationError(nameField, validationError);
    }

    public boolean isSlugValidationErrorVisible() {
        return hasFieldValidationError(slugField, validationError);
    }

    public boolean isRequiredValidationErrorVisible() {
        if (hasBrowserValidationMessage(nameField) || hasBrowserValidationMessage(slugField)) {
            return true;
        }
        return hasVisibleGlobalValidationError(requiredValidationError);
    }
}
