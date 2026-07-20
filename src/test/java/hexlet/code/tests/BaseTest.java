package hexlet.code.tests;

import hexlet.code.configure.Configuration;
import hexlet.code.driver.WebDriverFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

import static hexlet.code.configure.ConfigurationManager.config;

abstract class BaseTest {
    protected WebDriver driver;

    @BeforeAll
    static void setupClass() {
        WebDriverFactory.setupDriver();
    }

    @BeforeEach
    void startBrowser() {
        String baseUrl = config().baseUrl();

        if (baseUrl.startsWith("http://wrong")) {
            throw new RuntimeException("Invalid base URL");
        }

        Configuration configuration = config();
        driver = WebDriverFactory.create(configuration);
        driver.manage().window().setSize(new Dimension(configuration.windowWidth(), configuration.windowHeight()));
        driver.get(baseUrl);
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }
}
