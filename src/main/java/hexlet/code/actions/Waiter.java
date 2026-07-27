package hexlet.code.actions;

import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

import static hexlet.code.configure.ConfigurationManager.config;

public final class Waiter {
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(config().timeout());
    private static final Duration POLLING_INTERVAL = Duration.ofMillis(200);

    private final WebDriver driver;
    private final Duration timeout;

    public Waiter(WebDriver driver) {
        this(driver, DEFAULT_TIMEOUT);
    }

    public Waiter(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.timeout = timeout;
    }

    public void waitForPageLoaded() {
        Allure.step("Ожидание загрузки страницы",
                () -> createWait().until(webDriver -> Objects.equals(((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState"), "complete")));
    }

    public WebElement waitForVisible(By locator) {
        return createWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForVisible(WebElement element) {
        return createWait().until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement waitForClickable(By locator) {
        return createWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForClickable(WebElement element) {
        return createWait().until(ExpectedConditions.elementToBeClickable(element));
    }

    public void waitForInvisible(By locator) {
        createWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public void waitForInvisibleIfPresent(By locator) {
        if (!driver.findElements(locator).isEmpty()) {
            waitForInvisible(locator);
        }
    }

    public boolean waitForCondition(Function<WebDriver, Boolean> condition) {
        return createWait().until(condition);
    }

    public void waitForCondition(Function<WebDriver, Boolean> condition, Duration timeout) {
        createWait(timeout).until(condition);
    }

    public boolean waitForConditionOptional(Function<WebDriver, Boolean> condition, Duration timeout) {
        try {
            return createWait(timeout).until(condition);
        } catch (TimeoutException e) {
            return false;
        }
    }

    private Wait<WebDriver> createWait() {
        return createWait(timeout);
    }

    private Wait<WebDriver> createWait(Duration timeout) {
        return new FluentWait<>(driver)
                .withTimeout(timeout)
                .pollingEvery(POLLING_INTERVAL)
                .ignoring(NotFoundException.class)
                .ignoring(StaleElementReferenceException.class);
    }
}
