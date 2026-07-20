package hexlet.code.pages.labels;

import hexlet.code.components.SideBar;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LabelPage extends BasePage {
    private final By nameField = By.xpath("//*[@name='name']");
    private final By saveButton = By.xpath("//*[@aria-label='Save']");
    private final By editButton = By.xpath("//*[@aria-label='Edit']");
    private final By deleteButton = By.xpath("//*[@aria-label='Delete']");
    private final By successCreatePopup = By.xpath("//*[contains(text(), 'created')]");
    private final By successUpdatedPopup = By.xpath("//*[contains(text(), 'updated')]");
    private final By successDeletePopup = By.xpath("//*[contains(text(), 'deleted')]");
    private final By validationError = By.xpath("//*[contains(@class, 'MuiFormHelperText-root') and contains(@class, 'Mui-error')]");
    private final By requiredValidationError = By.xpath("//*[text()='Required']");

    public LabelPage(WebDriver driver) {
        super(driver);
    }

    public void fillLabelForm(Label label) {
        elementAction().find(nameField).waitUntilVisible().clearAndSendKeys(label.getName());
    }

    public LabelsListPage createLabelAndReturnToList(Label label) {
        fillLabelForm(label);
        elementAction().find(saveButton).waitUntilClickable().click();
        elementAction().find(successCreatePopup).waitUntilVisible();
        elementAction().find(successCreatePopup).waitUntilInvisible();
        LabelsListPage labelsListPage = new SideBar(getDriver()).getLabelsListPage();
        waiter().waitForCondition(driver -> labelsListPage.isLabelExists(label.getName()));
        return labelsListPage;
    }

    public LabelPage openEditForm() {
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

    public void updateLabel(Label label) {
        fillLabelForm(label);
        elementAction().find(saveButton).waitUntilClickable().click();
        elementAction().find(successUpdatedPopup).waitUntilVisible();
        elementAction().find(successUpdatedPopup).waitUntilInvisible();
    }

    public void submitFormWithoutWaitingForSuccess() {
        elementAction().find(saveButton).waitUntilClickable().click();
    }

    public void deleteLabel() {
        elementAction().find(deleteButton).waitUntilClickable().click();
        elementAction().find(successDeletePopup).waitUntilVisible();
    }

    public String getNameValue() {
        return elementAction().find(nameField).waitUntilVisible().getAttribute("value");
    }

    public boolean isNameFieldVisible() {
        return elementAction().find(nameField).waitUntilVisible().isDisplayed();
    }

    public boolean isSaveButtonVisible() {
        return elementAction().find(saveButton).waitUntilVisible().isDisplayed();
    }

    public boolean validationErrorIsDisplayed() {
        return hasValidationError();
    }

    public boolean hasValidationError() {
        return elementAction().hasFieldValidationError(nameField, validationError)
                || elementAction().hasVisibleGlobalValidationError(requiredValidationError);
    }

    public boolean isSaveButtonNotClickable() {
        try {
            waiter().waitForClickable(saveButton);
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}
