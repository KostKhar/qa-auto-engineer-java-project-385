package hexlet.code.pages;

import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPage extends BasePage {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginPage.class);

    private final By loginField = By.xpath("//*[@name='username']");
    private final By passwordField = By.xpath("//*[@name='password']");

    private final By signInButton = By.xpath("//*[text()='Sign in']");

    private final By errorMessage = By.xpath("//*[text()='Required']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public DashboardPage signInByLoginAndPassword(String username, String password) {
        Allure.step("Заполнение поля логин",
                () -> elementAction().find(loginField).waitUntilVisible().sendKeys(username));
        Allure.step("Заполнение поля пароль",
                () -> elementAction().find(passwordField).waitUntilVisible().sendKeys(password));
        Allure.step("Нажимаем на кнопку Sign in",
                () -> elementAction().find(signInButton).waitUntilClickable().click());
        waiter().waitForPageLoaded();
        return new DashboardPage(getDriver());
    }

    public String getErrorMessageText() {
        return elementAction().find(errorMessage).waitUntilVisible().getText();
    }

    public boolean isSignInButtonVisible() {
        try {
            return elementAction().find(signInButton).waitUntilVisible().isDisplayed();
        } catch (Exception e) {
            LOGGER.warn("Sign in button is not visible", e);
            return false;
        }
    }
}
