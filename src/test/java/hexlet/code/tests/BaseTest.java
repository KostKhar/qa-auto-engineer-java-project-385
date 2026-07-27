package hexlet.code.tests;

import hexlet.code.driver.WebDriverFactory;
import hexlet.code.tests.cleanup.CleanupExtension;
import hexlet.code.tests.cleanup.WebDriverExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

// Order matters: JUnit runs afterEach callbacks in reverse declaration order (LIFO),
// so WebDriverExtension must be declared first for its afterEach (driver.quit())
// to run *after* CleanupExtension's afterEach (which still needs a live driver).
@ExtendWith({WebDriverExtension.class, CleanupExtension.class})
public abstract class BaseTest {
    protected WebDriver driver;

    @BeforeAll
    static void setupClass() {
        WebDriverFactory.setupDriver();
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }
}
