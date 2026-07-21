package hexlet.code.pages;

import hexlet.code.components.Table;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static hexlet.code.configure.ConfigurationManager.config;

public abstract class AbstractListPage<T> extends BasePage {
    protected static final By TABLE_CONTAINER = By.className("RaList-main");
    protected static final By LIST_ROOT = By.xpath(
            "//*[@aria-label='Create']/ancestor::div[contains(@class, 'RaList')][1]"
    );

    private Table<T> table;

    protected AbstractListPage(WebDriver driver) {
        super(driver);
    }

    protected Table<T> getTable() {
        return table;
    }

    protected static WebElement waitForTableContainer(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(config().timeout()));
        List<WebElement> containers = driver.findElements(TABLE_CONTAINER);
        if (!containers.isEmpty()) {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(TABLE_CONTAINER));
        }
        return wait.until(ExpectedConditions.visibilityOfElementLocated(LIST_ROOT));
    }

    protected static WebElement waitForListTableContainer(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(config().timeout()));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(TABLE_CONTAINER));
    }


}
