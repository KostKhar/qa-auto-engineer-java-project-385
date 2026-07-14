package hexlet.code.pages;

import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

import static hexlet.code.configure.ConfigurationManager.config;

public abstract class BasePage {
    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(config().timeout());
    private static final By SNACKBAR = By.xpath("//*[contains(@class,'MuiSnackbar-root')]");
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
        setReactInputValue(element, "");
        if (text != null && !text.isEmpty()) {
            element.sendKeys(text);
        }
    }

    private void setReactInputValue(WebElement element, String value) {
        ((JavascriptExecutor) getDriver()).executeScript(
                """
                const el = arguments[0];
                const value = arguments[1];
                const prototype = el.tagName === 'TEXTAREA'
                    ? window.HTMLTextAreaElement.prototype
                    : window.HTMLInputElement.prototype;
                const setter = Object.getOwnPropertyDescriptor(prototype, 'value').set;
                setter.call(el, value);
                el.dispatchEvent(new Event('input', { bubbles: true }));
                el.dispatchEvent(new Event('change', { bubbles: true }));
                """,
                element, value);
    }

    protected void waitForElementAndClick(By locator) {
        clickElement(locator);
    }

    protected void clickElement(By locator) {
        clickElement(waitForElementClickable(locator));
    }

    protected void clickElement(WebElement element) {
        scrollIntoView(element);
        WebElement clickable = getWait().until(ExpectedConditions.elementToBeClickable(element));
        try {
            clickable.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", clickable);
        }
    }

    protected void waitForSnackbarToDisappear() {
        if (!getDriver().findElements(SNACKBAR).isEmpty()) {
            waitForElementInvisible(SNACKBAR);
        }
    }

    protected void waitForElementAndSendKeys(By locator, String text) {
        waitForElementVisible(locator).sendKeys(text);
    }


    protected boolean waitForCondition(Function<WebDriver, Boolean> condition) {
        return getWait().until(condition);
    }

    protected boolean waitForCondition(Function<WebDriver, Boolean> condition, Duration timeout) {
        return new WebDriverWait(getDriver(), timeout).until(condition);
    }

    protected boolean waitForConditionOptional(Function<WebDriver, Boolean> condition, Duration timeout) {
        try {
            return new WebDriverWait(getDriver(), timeout).until(condition);
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected void dragAndDrop(WebElement source, WebElement target) {
        scrollIntoView(source);
        scrollIntoView(target);
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript(
                """
                const source = arguments[0];
                const target = arguments[1];
                const dataTransfer = new DataTransfer();
                const fire = (element, type) => element.dispatchEvent(
                    new DragEvent(type, { bubbles: true, cancelable: true, dataTransfer })
                );
                fire(source, 'dragstart');
                fire(target, 'dragenter');
                fire(target, 'dragover');
                fire(target, 'drop');
                fire(source, 'dragend');
                """,
                source,
                target
        );
    }


    protected void waitForPageLoaded() {
        Allure.step("Ожидание загрузки страницы", () -> getWait().until(webDriver ->
                Objects.equals(((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState"), "complete")
        ));
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) getDriver()).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element
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

    protected void selectComboboxOption(By combobox, String optionText) {
        WebElement field = waitForElementClickable(combobox);
        if (field.getText().trim().equals(optionText)) {
            return;
        }
        clickElement(field);
        By option = By.xpath(
                "//*[@role='listbox']//*[@role='option'][normalize-space(.)=" + xpathLiteral(optionText) + "]"
        );
        clickElement(option);
        waitForSnackbarToDisappear();
    }

    protected boolean hasFieldValidationError(By field, By validationErrorLocator) {
        try {
            return hasBrowserValidationMessage(field) || hasVisibleValidationError(field, validationErrorLocator);
        } catch (StaleElementReferenceException e) {
            return hasBrowserValidationMessage(field) || hasVisibleValidationError(field, validationErrorLocator);
        }
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
            waitForElementVisible(field);
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
