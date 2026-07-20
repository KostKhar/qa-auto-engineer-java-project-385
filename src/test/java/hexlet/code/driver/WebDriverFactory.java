package hexlet.code.driver;

import hexlet.code.configure.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Files;
import java.nio.file.Path;

public final class WebDriverFactory {
    private static final Path SYSTEM_CHROMEDRIVER = Path.of("/usr/bin/chromedriver");
    private static final Path SYSTEM_CHROMIUM = Path.of("/usr/bin/chromium");

    private WebDriverFactory() {
    }

    public static void setupDriver() {
        if (Files.exists(SYSTEM_CHROMEDRIVER)) {
            System.setProperty("webdriver.chrome.driver", SYSTEM_CHROMEDRIVER.toString());
            return;
        }

        WebDriverManager.chromedriver().setup();
    }

    public static WebDriver create(Configuration configuration) {
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
