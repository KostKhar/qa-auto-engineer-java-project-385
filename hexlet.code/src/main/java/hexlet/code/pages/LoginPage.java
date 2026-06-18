package hexlet.code.pages;

import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private final By loginField = By.xpath("//*[@name='username']");
    private final By passwordField = By.xpath("//*[@name='password']");

    private final By signInButton = By.xpath("//*[text()='Sign in']");

    private final By errorMessage = By.xpath("//*[text()='Required']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public DashboardPage signInByLoginAndPassword(String username, String password) {
        Allure.step("Заполнение поля логин", () -> waitForElementAndSendKeys(loginField, username));
        Allure.step("Заполнение поля пароль", () -> waitForElementAndSendKeys(passwordField, password));
        Allure.step("Нажимаем на кнопку Sign in", () -> waitForElementAndClick(signInButton));
        waitForPageLoaded();
        return new DashboardPage(driver);
    }

    public By getErrorMessage() {
        return errorMessage;
    }

    public boolean isSignInButtonVisible() {
        try {
            return waitForElementVisible(signInButton).isDisplayed();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
