package hexlet.code.pages.labels;

import hexlet.code.components.SideBar;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.*;

import java.util.List;

public class LabelPage extends BasePage {
    private final By nameField = By.xpath("//*[@name='name']");
    private final By slugField = By.xpath("//*[@name='slug']");
    private final By saveButton = By.xpath("//*[@aria-label='Save']");
    private final By editButton = By.xpath("//*[@aria-label='Edit']");
    private final By deleteButton = By.xpath("//*[@aria-label='Delete']");
    private final By confirmDeleteButton = By.xpath("//*[@role='dialog']//button[contains(text(), 'Confirm')]");
    private final By successCreatePopup = By.xpath("//*[contains(text(), 'Element created')]");
    private final By successUpdatedPopup = By.xpath("//*[contains(text(), 'Element updated')]");
    private final By successDeletePopup = By.xpath("//*[contains(text(), 'Element deleted')]");
    private final By validationError = By.xpath(
            "//*[contains(@class, 'MuiFormHelperText-root') and contains(@class, 'Mui-error')]"
    );
    private final By requiredValidationError = By.xpath("//*[text()='Required']");

    public LabelPage(WebDriver driver) {
        super(driver);
    }

    public void fillLabelForm(Label label) {
        waitForElementClearAndSendKeys(nameField, label.getName());
        waitForElementClearAndSendKeys(slugField, label.getSlug());
    }

    public LabelsListPage createLabelAndReturnToList(Label label) {
        fillLabelForm(label);
        waitForElementClickable(saveButton).click();
        waitForElementVisible(successCreatePopup);
        return new SideBar(driver).getLabelsListPage();
    }

    public LabelPage openEditForm() {
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

    public boolean updateLabel(Label label) {
        fillLabelForm(label);
        waitForElementClickable(saveButton).click();
        return waitForElementVisible(successUpdatedPopup).isDisplayed();
    }

    public void submitFormWithoutWaitingForSuccess() {
        waitForElementClickable(saveButton).click();
    }

    public boolean deleteLabel() {
        waitForElementClickable(deleteButton).click();
        waitForElementClickable(confirmDeleteButton).click();
        waitForElementVisible(successDeletePopup);
        return true;
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
        return hasBrowserValidationMessage(nameField) || hasVisibleValidationError(nameField);
    }

    public boolean isSlugValidationErrorVisible() {
        return hasBrowserValidationMessage(slugField) || hasVisibleValidationError(slugField);
    }

    public boolean isRequiredValidationErrorVisible() {
        if (hasBrowserValidationMessage(nameField) || hasBrowserValidationMessage(slugField)) {
            return true;
        }

        try {
            waitForElementVisible(requiredValidationError);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private boolean hasBrowserValidationMessage(By field) {
        WebElement element = waitForElementVisible(field);
        Object message = ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].validationMessage;", element
        );
        return message != null && !message.toString().isBlank();
    }

    private boolean hasVisibleValidationError(By field) {
        try {
            WebElement element = waitForElementVisible(field);
            List<WebElement> errors = element.findElements(By.xpath(
                    "./ancestor::div[contains(@class, 'MuiFormControl-root')]"
                            + "//*[contains(@class, 'MuiFormHelperText-root') and contains(@class, 'Mui-error')]"
            ));
            if (errors.stream().anyMatch(WebElement::isDisplayed)) {
                return true;
            }

            List<WebElement> globalErrors = driver.findElements(validationError);
            return globalErrors.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return false;
        }
    }
}
