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

import java.nio.file.Files;
import java.nio.file.Path;

import static hexlet.code.configure.ConfigurationManager.config;

abstract class BasePageTest {
    private static final Path SYSTEM_CHROMEDRIVER = Path.of("/usr/bin/chromedriver");
    private static final Path SYSTEM_CHROMIUM = Path.of("/usr/bin/chromium");

    protected WebDriver driver;

    @BeforeAll
    static void setupClass() {
        if (Files.exists(SYSTEM_CHROMEDRIVER)) {
            System.setProperty("webdriver.chrome.driver", SYSTEM_CHROMEDRIVER.toString());
            return;
        }

        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void startBrowser() {
        String baseUrl = System.getenv("APP_BASE_URL");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "http://localhost:5173/";
        }

        Configuration configuration = config();
        driver = createWebDriver(configuration);
        driver.manage().window().setSize(new Dimension(configuration.windowWidth(), configuration.windowHeight()));
        driver.get(baseUrl);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private WebDriver createWebDriver(Configuration configuration) {
        ChromeOptions options = new ChromeOptions();
        if (configuration.headless()) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
        if (Files.exists(SYSTEM_CHROMIUM)) {
            options.setBinary(SYSTEM_CHROMIUM.toString());
        }
        return new ChromeDriver(options);
    }

}
