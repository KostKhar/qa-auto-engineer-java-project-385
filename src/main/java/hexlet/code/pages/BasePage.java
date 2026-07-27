package hexlet.code.pages;

import hexlet.code.actions.ElementAction;
import hexlet.code.actions.Waiter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class BasePage {
    private static final By SNACKBAR = By.xpath("//*[contains(@class,'MuiSnackbar-root')]");

    private final WebDriver driver;
    private final Waiter waiter;
    private final ElementAction elementAction;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.waiter = new Waiter(driver);
        this.elementAction = new ElementAction(driver, waiter);
        waiter.waitForPageLoaded();
    }

    protected WebDriver getDriver() {
        return driver;
    }

    protected Waiter waiter() {
        return waiter;
    }

    protected ElementAction elementAction() {
        return elementAction;
    }

    protected ElementAction elementAction(WebElement element) {
        return new ElementAction(driver, waiter, element);
    }

    protected void waitForSnackbarToDisappear() {
        waiter.waitForInvisibleIfPresent(SNACKBAR);
    }

    protected void initComponents() {
    }
}
