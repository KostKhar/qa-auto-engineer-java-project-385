package hexlet.code.components;

import hexlet.code.pages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static hexlet.code.config.ConfigurationManager.config;

public class Header {
    private final WebDriver driver;
    private final By menuIcon = By.xpath("//*[@data-testid='MenuIcon']");
    private final By toggleLigthDark = By.xpath("//*[@aria-label='Toggle light/dark mode']");
    private final By profileButton = By.xpath("//*[@aria-label='Profile']");
    private final By logoutButton = By.xpath("//*[@data-testid='PowerSettingsNewIcon']");
    public Header(WebDriver driver) {
        this.driver = driver;
    }

    public LoginPage clickLogoutButton() {
        driver.findElement(profileButton).click();
        new WebDriverWait(driver, Duration.ofSeconds(config().timeout()))
                .until(ExpectedConditions.elementToBeClickable(logoutButton));
        driver.findElement(logoutButton).click();
        return new LoginPage(driver);
    }
}
