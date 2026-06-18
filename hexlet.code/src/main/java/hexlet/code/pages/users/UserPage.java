package hexlet.code.pages.users;

import hexlet.code.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class UserPage extends BasePage {
    private final By emailField = By.xpath("//*[@name='email']");
    private final By firstNameField = By.xpath("//*[@name='firstName']");
    private final By lastNameField = By.xpath("//*[@name='lastName']");

    private final By saveButton = By.xpath("//*[@aria-label='Save']");
    private final By deleteButton = By.xpath("//*[@aria-label='Delete']");
    private final By showButton = By.xpath("//*[@aria-label='Show']");

    private final By successUpdatedPopup = By.xpath("//*[@text='Element updated']");
    private final By successDeletePopup = By.xpath("//*[@text='Element deleted']");


    protected UserPage(WebDriver driver) {
        super(driver);
    }

    public boolean createUser(User user) {
        waitForElementAndSendKeys(emailField, user.getEmail());
        waitForElementAndSendKeys(firstNameField, user.getFirstname());
        waitForElementAndSendKeys(lastNameField, user.getLastname());
        waitForElementClickable(saveButton);
        driver.findElement(saveButton).click();
        return driver.findElement(successUpdatedPopup).isDisplayed();
    }

    public boolean deleteUser() {
        driver.findElement(deleteButton).click();
        return driver.findElement(successDeletePopup).isDisplayed();
    }

    public By getEmailField() {
        return emailField;
    }

    public By getFirstNameField() {
        return firstNameField;
    }

    public By getSaveButton() {
        return saveButton;
    }

    public By getLastNameField() {
        return lastNameField;
    }

    public By getDeleteButton() {
        return deleteButton;
    }

    public By getShowButton() {
        return showButton;
    }
}
