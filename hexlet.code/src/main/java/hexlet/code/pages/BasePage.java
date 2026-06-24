package hexlet.code.pages;

import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

import static hexlet.code.config.ConfigurationManager.config;

public abstract class BasePage {
    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(config().timeout());
    protected WebDriver driver;
    protected WebDriverWait wait;


    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        waitForPageLoaded();
    }

    protected void initComponents() {
    }

    protected WebElement waitForElementVisible(By locator) {
        return Allure.step("Ожидание видимости элемента",
                () -> wait.until(ExpectedConditions.visibilityOfElementLocated(locator)));
    }

    protected WebElement waitForElementClickable(By locator) {
        return Allure.step("Ожидание кликабельности элемента",
                () -> wait.until(ExpectedConditions.elementToBeClickable(locator)));
    }


    protected void waitForElementClearAndSendKeys(By locator, String text) {
        WebElement element = waitForElementVisible(locator);
        element.clear();
        element.sendKeys(text);
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
        Allure.step("Ожидание загрузки страницы", () -> wait.until(webDriver ->
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

    protected String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        return "concat('" + value.replace("'", "',\"'\",'") + "')";
    }
}
