package hexlet.code.tests;

import hexlet.code.config.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static hexlet.code.config.ConfigurationManager.config;

abstract class BasePageTest {
    protected WebDriver driver;

    @BeforeEach
    void startBrowser() {
        Configuration configuration = config();
        driver = createWebDriver(configuration);
        driver.manage().window().setSize(new Dimension(configuration.windowWidth(), configuration.windowHeight()));
        driver.get(configuration.baseUrl());
    }

    @AfterEach
    void tearDownAll() {
        if (driver != null) {
            driver.quit();
        }
    }

    private WebDriver createWebDriver(Configuration configuration) {
        String browser = configuration.browser().toLowerCase();

        return switch (browser) {
            case "chromium", "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (configuration.headless()) {
                    options.addArguments("--headless=new");
                }
                yield new ChromeDriver(options);
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }
}
