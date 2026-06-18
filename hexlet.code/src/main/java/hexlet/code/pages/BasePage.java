package hexlet.code.pages;

import io.qameta.allure.Allure;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

import static hexlet.code.config.ConfigurationManager.config;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(config().timeout());


    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        waitForPageLoaded();
    }

    protected void initComponents() {
    }

    protected WebElement waitForElementVisible(By locator) {
        return Allure.step("Ожидание видимости элемента",
                ()-> wait.until(ExpectedConditions.visibilityOfElementLocated(locator)));
    }

    protected WebElement waitForElementClickable(By locator) {
        return Allure.step("Ожидание кликабельности элемента",
                ()-> wait.until(ExpectedConditions.elementToBeClickable(locator)));
    }

    protected WebElement waitForElementPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected boolean waitForElementInvisible(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected void waitForTextToBePresent(By locator, String text) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    protected void waitForAttributeToContain(By locator, String attribute, String value) {
        wait.until(ExpectedConditions.attributeContains(locator, attribute, value));
    }


    protected void waitForElementAndClick(By locator) {
        waitForElementClickable(locator).click();
    }

    protected void waitForElementAndSendKeys(By locator, String text) {
        waitForElementVisible(locator).sendKeys(text);
    }

    protected String waitForElementAndGetText(By locator) {
        return waitForElementVisible(locator).getText();
    }


    protected boolean waitForCondition(Function<WebDriver, Boolean> condition) {
        return wait.until(condition);
    }


    protected void waitForPageLoaded() {
        Allure.step("Ожидание загрузки страницы",()->  wait.until(webDriver ->
                Objects.equals(((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState"), "complete")
        ));
    }

    protected void waitForAjaxComplete() {
        wait.until(webDriver ->
                (Boolean) ((JavascriptExecutor) webDriver)
                        .executeScript("return jQuery.active == 0")
        );
    }
}
