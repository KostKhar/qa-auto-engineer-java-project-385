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
            elementAction().find(nameField).clearAndSendKeys(status.getName());
        }
        if (status.getSlug() != null) {
            elementAction().find(slugField).clearAndSendKeys(status.getSlug());
        }
    }

    public StatusesListPage createStatusAndReturnToList(Status status) {
        fillStatusForm(status);
        elementAction().find(saveButton).click();
        waiter().waitForVisible(successCreatePopup);
        waiter().waitForInvisible(successCreatePopup);
        StatusesListPage statusesListPage = new SideBar(getDriver()).getStatusesListPage();
        waiter().waitForCondition(driver -> statusesListPage.isStatusExists(status.getName()));
        return statusesListPage;
    }

    public StatusPage openEditForm() {
        if (isEditFormOpen()) {
            return this;
        }
        elementAction().find(editButton).click();
        waiter().waitForVisible(nameField);
        return this;
    }

    private boolean isEditFormOpen() {
        try {
            WebElement field = waiter().waitForVisible(nameField);
            return field.isDisplayed() && field.isEnabled() && "input".equalsIgnoreCase(field.getTagName());
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean updateStatus(Status status) {
        fillStatusForm(status);
        elementAction().find(saveButton).click();
        waiter().waitForVisible(successUpdatedPopup);
        waiter().waitForInvisible(successUpdatedPopup);
        return true;
    }

    public void submitFormWithoutWaitingForSuccess() {
        elementAction().find(saveButton).click();
    }

    public String getNameValue() {
        return elementAction().find(nameField).getAttribute("value");
    }

    public String getSlugValue() {
        return elementAction().find(slugField).getAttribute("value");
    }

    public boolean isNameFieldVisible() {
        return elementAction().find(nameField).isEnabled();
    }

    public boolean isSlugFieldVisible() {
        return elementAction().find(slugField).isEnabled();
    }

    public boolean isSaveButtonVisible() {
        return elementAction().find(saveButton).isEnabled();
    }

    public boolean hasValidationError() {
        return isNameValidationErrorVisible()
                || isSlugValidationErrorVisible()
                || isRequiredValidationErrorVisible();
    }

    public boolean isNameValidationErrorVisible() {
        return elementAction().hasFieldValidationError(nameField, validationError);
    }

    public boolean isSlugValidationErrorVisible() {
        return elementAction().hasFieldValidationError(slugField, validationError);
    }

    public boolean isRequiredValidationErrorVisible() {
        if (elementAction().hasBrowserValidationMessage(nameField)
                || elementAction().hasBrowserValidationMessage(slugField)) {
            return true;
        }
        return elementAction().hasVisibleGlobalValidationError(requiredValidationError);
    }
}
