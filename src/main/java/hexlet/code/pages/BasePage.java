package hexlet.code.pages;

import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static hexlet.code.configure.ConfigurationManager.config;

public abstract class BasePage {
    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(config().timeout());
    private WebDriver driver;
    private WebDriverWait wait;


    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        waitForPageLoaded();
    }

    protected WebDriver getDriver() {
        return driver;
    }

    protected WebDriverWait getWait() {
        return wait;
    }

    protected void initComponents() {
    }

    protected WebElement waitForElementVisible(By locator) {
        return Allure.step("Ожидание видимости элемента",
                () -> getWait().until(ExpectedConditions.visibilityOfElementLocated(locator)));
    }

    protected WebElement waitForElementClickable(By locator) {
        return Allure.step("Ожидание кликабельности элемента",
                () -> getWait().until(ExpectedConditions.elementToBeClickable(locator)));
    }

    protected void waitForElementInvisible(By locator) {
         Allure.step("Ожидание исчезновения элемента",
                () -> getWait().until(ExpectedConditions.invisibilityOfElementLocated(locator)));
    }


    protected void waitForElementClearAndSendKeys(By locator, String text) {
        WebElement element = waitForElementVisible(locator);
        String selectAll;
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            selectAll = Keys.chord(Keys.COMMAND, "a");
        } else {
            selectAll = Keys.chord(Keys.CONTROL, "a");
        }
        element.sendKeys(selectAll);
        element.sendKeys(Keys.DELETE);
        element.sendKeys(text);
    }

    protected void waitForElementAndClick(By locator) {
        waitForElementClickable(locator).click();
    }

    protected void waitForElementAndSendKeys(By locator, String text) {
        waitForElementVisible(locator).sendKeys(text);
    }


    protected boolean waitForCondition(Function<WebDriver, Boolean> condition) {
        return getWait().until(condition);
    }


    protected void waitForPageLoaded() {
        Allure.step("Ожидание загрузки страницы", () -> getWait().until(webDriver ->
                Objects.equals(((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState"), "complete")
        ));
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

    protected void selectComboboxOption(By combobox, String optionText) {
        WebElement field = waitForElementClickable(combobox);
        if (field.getText().trim().equals(optionText)) {
            return;
        }
        field.click();
        By option = By.xpath(
                "//*[@role='listbox']//*[@role='option'][normalize-space(.)=" + xpathLiteral(optionText) + "]"
        );
        waitForElementClickable(option).click();
    }

    protected boolean hasBrowserValidationMessage(By field) {
        WebElement element = waitForElementVisible(field);
        Object message = ((JavascriptExecutor) getDriver()).executeScript(
                "return arguments[0].validationMessage;", element
        );
        return message != null && !message.toString().isBlank();
    }

    protected boolean hasVisibleValidationError(By field, By validationErrorLocator) {
        try {
            WebElement element = waitForElementVisible(field);
            List<WebElement> errors = element.findElements(validationErrorLocator);
            if (errors.stream().anyMatch(WebElement::isDisplayed)) {
                return true;
            }
            return getDriver().findElements(validationErrorLocator).stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean hasVisibleGlobalValidationError(By validationErrorLocator) {
        try {
            waitForElementVisible(validationErrorLocator);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
