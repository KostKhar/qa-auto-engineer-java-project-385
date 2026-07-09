package hexlet.code.tests;

import hexlet.code.configure.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.Browser;

import java.nio.file.Files;
import java.nio.file.Path;

import static hexlet.code.configure.ConfigurationManager.config;

abstract class BasePageTest {

    protected WebDriver driver;

    @BeforeAll
    static void setupBrowser() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void startBrowser() {
        Configuration configuration = config();
        driver = createWebDriver(configuration);
        driver.manage().window().setSize(new Dimension(configuration.windowWidth(), configuration.windowHeight()));
        driver.get(configuration.baseUrl());
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private WebDriver createWebDriver(Configuration configuration) {
        String browser = configuration.browser().toLowerCase();

        return switch (browser) {
            case "chromium", "chrome" -> {
                ChromeOptions options = new ChromeOptions();
                if (configuration.headless()) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
                yield new ChromeDriver(options);
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }
}
