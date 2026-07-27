package hexlet.code.actions;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementAction {
    private final WebDriver driver;
    private final Waiter waiter;
    private By locator;
    private WebElement element;

    public ElementAction(WebDriver driver) {
        this(driver, new Waiter(driver));
    }

    public ElementAction(WebDriver driver, Waiter waiter) {
        this.driver = driver;
        this.waiter = waiter;
    }

    public ElementAction(WebDriver driver, WebElement element) {
        this(driver, new Waiter(driver), element);
    }

    public ElementAction(WebDriver driver, Waiter waiter, WebElement element) {
        this(driver, waiter);
        this.element = element;
    }

    public ElementAction find(By locator) {
        this.locator = locator;
        waiter.waitForVisible(locator);
        this.element = driver.findElement(locator);
        return this;
    }

    public ElementAction click() {
        if (locator != null) {
            element = waiter.waitForClickable(locator);
        } else {
            scrollIntoView();
            element = waiter.waitForClickable(element);
        }
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
        return this;
    }

    public ElementAction sendKeys(String text) {
        ensureElement();
        if (text != null) {
            element.sendKeys(text);
        }
        return this;
    }

    public ElementAction clearAndSendKeys(String text) {
        ensureElement();
        setReactInputValue(element, "");
        if (text != null && !text.isEmpty()) {
            element.sendKeys(text);
        }
        return this;
    }

    public String getText() {
        ensureElement();
        return element.getText();
    }

    public String getAttribute(String name) {
        ensureElement();
        return element.getAttribute(name);
    }

    public boolean isDisplayed() {
        ensureElement();
        return element.isDisplayed();
    }

    public ElementAction scrollIntoView() {
        WebElement target;
        if (element != null) {
            target = element;
        } else {
            target = waiter.waitForVisible(locator);
        }
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'center'});", target
        );
        if (element == null) {
            element = target;
        }
        return this;
    }

    public void scrollIntoView(WebElement target) {
        new ElementAction(driver, waiter, target).scrollIntoView();
    }

    public void dragAndDrop(WebElement source, WebElement target) {
        scrollIntoView(source);
        scrollIntoView(target);
        JavascriptExecutor js = (JavascriptExecutor) driver;
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

    public void selectComboboxOption(By combobox, String optionText) {
        WebElement field = waiter.waitForClickable(combobox);
        if (field.getText().trim().equals(optionText)) {
            return;
        }
        new ElementAction(driver, waiter, field).click();
        By option = By.xpath(
                "//*[@role='listbox']//*[@role='option'][normalize-space(.)=" + xpathLiteral(optionText) + "]"
        );
        find(option).click();
    }

    public boolean hasFieldValidationError(By field, By validationErrorLocator) {
        try {
            return hasBrowserValidationMessage(field) || hasVisibleValidationError(field, validationErrorLocator);
        } catch (StaleElementReferenceException e) {
            return hasBrowserValidationMessage(field) || hasVisibleValidationError(field, validationErrorLocator);
        }
    }

    public boolean hasBrowserValidationMessage(By field) {
        WebElement fieldElement = waiter.waitForVisible(field);
        Object message = ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].validationMessage;", fieldElement
        );
        return message != null && !message.toString().isBlank();
    }

    public boolean hasVisibleValidationError(By field, By validationErrorLocator) {
        try {
            waiter.waitForVisible(field);
            return driver.findElements(validationErrorLocator).stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasVisibleGlobalValidationError(By validationErrorLocator) {
        try {
            waiter.waitForVisible(validationErrorLocator);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public static String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        return "concat('" + value.replace("'", "',\"'\",'") + "')";
    }

    private void ensureElement() {
        if (element == null && locator != null) {
            element = waiter.waitForVisible(locator);
        }
    }

    private void setReactInputValue(WebElement element, String value) {
        ((JavascriptExecutor) driver).executeScript(
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
}
