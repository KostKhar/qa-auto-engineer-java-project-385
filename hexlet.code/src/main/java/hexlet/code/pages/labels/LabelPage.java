package hexlet.code.pages.labels;

import hexlet.code.components.SideBar;
import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static hexlet.code.config.ConfigurationManager.config;

public class LabelPage extends BasePage {
    private final By nameField = By.xpath("//*[@name='name']");
    private final By saveButton = By.xpath("//*[@aria-label='Save']");
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
        return new SideBar(driver).getLabelsListPage();
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
        waitForElementVisible(successDeletePopup);
        return true;
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
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(config().timeout()));
            wait.until(ExpectedConditions.elementToBeClickable(saveButton));
            return false;
        } catch (Exception e) {
            return true;
        }
    }

}
