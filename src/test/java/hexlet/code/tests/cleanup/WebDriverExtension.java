package hexlet.code.tests.cleanup;

import hexlet.code.driver.WebDriverFactory;
import hexlet.code.tests.BaseTest;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

import static hexlet.code.configure.ConfigurationManager.config;

/**
 * Creates the browser before each test and quits it after the test (and after
 * {@link CleanupExtension} has had a chance to clean up using a live driver).
 * Must be declared <b>before</b> {@link CleanupExtension} in {@code @ExtendWith} on {@link BaseTest}
 * — see {@link CleanupExtension} javadoc for why order matters here.
 */
public class WebDriverExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        String baseUrl = config().baseUrl();

        WebDriver driver = WebDriverFactory.create();
        driver.get(baseUrl);
        resolveBaseTest(context).setDriver(driver);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        BaseTest baseTest = resolveBaseTest(context);
        WebDriver driver = baseTest.getDriver();
        if (driver != null) {
            driver.quit();
        }
    }

    private static BaseTest resolveBaseTest(ExtensionContext context) {
        Object testInstance = context.getRequiredTestInstance();
        if (testInstance instanceof BaseTest baseTest) {
            return baseTest;
        }
        throw new IllegalStateException("WebDriverExtension requires BaseTest test instance");
    }
}
