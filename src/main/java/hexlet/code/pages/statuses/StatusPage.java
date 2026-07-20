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
            elementAction().find(nameField).waitUntilVisible().clearAndSendKeys(status.getName());
        }
        if (status.getSlug() != null) {
            elementAction().find(slugField).waitUntilVisible().clearAndSendKeys(status.getSlug());
        }
    }

    public StatusesListPage createStatusAndReturnToList(Status status) {
        fillStatusForm(status);
        elementAction().find(saveButton).waitUntilClickable().click();
        elementAction().find(successCreatePopup).waitUntilVisible();
        elementAction().find(successCreatePopup).waitUntilInvisible();
        StatusesListPage statusesListPage = new SideBar(getDriver()).getStatusesListPage();
        waiter().waitForCondition(driver -> statusesListPage.isStatusExists(status.getName()));
        return statusesListPage;
    }

    public StatusPage openEditForm() {
        if (isEditFormOpen()) {
            return this;
        }
        elementAction().find(editButton).waitUntilClickable().click();
        elementAction().find(nameField).waitUntilVisible();
        return this;
    }

    private boolean isEditFormOpen() {
        try {
            WebElement field = elementAction().find(nameField).waitUntilVisible().getElement();
            return field.isDisplayed() && field.isEnabled() && "input".equalsIgnoreCase(field.getTagName());
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean updateStatus(Status status) {
        fillStatusForm(status);
        elementAction().find(saveButton).waitUntilClickable().click();
        elementAction().find(successUpdatedPopup).waitUntilVisible();
        elementAction().find(successUpdatedPopup).waitUntilInvisible();
        return true;
    }

    public void submitFormWithoutWaitingForSuccess() {
        elementAction().find(saveButton).waitUntilClickable().click();
    }

    public String getNameValue() {
        return elementAction().find(nameField).waitUntilVisible().getAttribute("value");
    }

    public String getSlugValue() {
        return elementAction().find(slugField).waitUntilVisible().getAttribute("value");
    }

    public boolean isNameFieldVisible() {
        return elementAction().find(nameField).waitUntilVisible().isDisplayed();
    }

    public boolean isSlugFieldVisible() {
        return elementAction().find(slugField).waitUntilVisible().isDisplayed();
    }

    public boolean isSaveButtonVisible() {
        return elementAction().find(saveButton).waitUntilVisible().isDisplayed();
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
