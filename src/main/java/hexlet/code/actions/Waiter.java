package hexlet.code.actions;

import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

import static hexlet.code.configure.ConfigurationManager.config;

public final class Waiter {
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(config().timeout());
    private static final By SNACKBAR = By.xpath("//*[contains(@class,'MuiSnackbar-root')]");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public Waiter(WebDriver driver) {
        this(driver, DEFAULT_TIMEOUT);
    }

    public Waiter(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
    }

    public WebDriver getDriver() {
        return driver;
    }

    public WebDriverWait getWait() {
        return wait;
    }

    public void waitForPageLoaded() {
        Allure.step("Ожидание загрузки страницы",
                () -> wait.until(webDriver -> Objects.equals(((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState"), "complete")));
    }

    public WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void waitForInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public void waitForSnackbarToDisappear() {
        if (!driver.findElements(SNACKBAR).isEmpty()) {
            waitForInvisible(SNACKBAR);
        }
    }

    public boolean waitForCondition(Function<WebDriver, Boolean> condition) {
        return wait.until(condition);
    }

    public boolean waitForCondition(Function<WebDriver, Boolean> condition, Duration timeout) {
        return new WebDriverWait(driver, timeout).until(condition);
    }

    public boolean waitForConditionOptional(Function<WebDriver, Boolean> condition, Duration timeout) {
        try {
            return new WebDriverWait(driver, timeout).until(condition);
        } catch (TimeoutException e) {
            return false;
        }
    }
}
