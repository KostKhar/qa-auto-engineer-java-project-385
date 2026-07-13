package hexlet.code.pages.labels;

import hexlet.code.components.SideBar;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static hexlet.code.configure.ConfigurationManager.config;

public class LabelPage extends BasePage {
    private final By nameField = By.xpath("//*[@name='name']");
    private final By saveButton = By.xpath("//*[@aria-label='Save']");
    private final By editButton = By.xpath("//*[@aria-label='Edit']");
    private final By deleteButton = By.xpath("//*[@aria-label='Delete']");
    private final By successCreatePopup = By.xpath("//*[contains(text(), 'created')]");
    private final By successUpdatedPopup = By.xpath("//*[contains(text(), 'updated')]");
    private final By successDeletePopup = By.xpath("//*[contains(text(), 'deleted')]");
    private final By validationError = By.xpath("//*[contains(@class, 'MuiFormHelperText-root') and contains(@class, 'Mui-error')]");

    public LabelPage(WebDriver driver) {
        super(driver);
    }

    public void fillLabelForm(Label label) {
        waitForElementClearAndSendKeys(nameField, label.getName());
    }

    public LabelsListPage createLabelAndReturnToList(Label label) {
        fillLabelForm(label);
        waitForElementClickable(saveButton).click();
        waitForElementVisible(successCreatePopup);
        LabelsListPage labelsListPage = new SideBar(getDriver()).getLabelsListPage();
        if (labelsListPage.getLabelByName(label.getName()) == null) {
            throw new IllegalStateException(
                    String.format("Label '%s' was created but not found in list", label.getName())
            );
        }
        return labelsListPage;
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

    public void updateLabel(Label label) {
        fillLabelForm(label);
        waitForElementClickable(saveButton).click();
        waitForElementVisible(successUpdatedPopup).isDisplayed();
    }

    public void submitFormWithoutWaitingForSuccess() {
        waitForElementClickable(saveButton).click();
    }

    public void deleteLabel() {
        waitForElementClickable(deleteButton).click();
        waitForElementVisible(successDeletePopup);
    }

    public String getNameValue() {
        return waitForElementVisible(nameField).getAttribute("value");
    }


    public boolean isNameFieldVisible() {
        return waitForElementVisible(nameField).isDisplayed();
    }

    public boolean isSaveButtonVisible() {
        return waitForElementVisible(saveButton).isDisplayed();
    }

    public boolean validationErrorIsDisplayed() {
        return waitForElementVisible(validationError).isDisplayed();
    }

    public boolean isSaveButtonNotClickable() {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(config().timeout()));
            wait.until(ExpectedConditions.elementToBeClickable(saveButton));
            return false;
        } catch (Exception e) {
            return true;
        }
    }

}
