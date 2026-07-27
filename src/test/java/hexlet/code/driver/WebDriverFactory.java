package hexlet.code.driver;

import hexlet.code.configure.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static hexlet.code.configure.ConfigurationManager.config;

public final class WebDriverFactory {
    private static final Path SYSTEM_CHROMEDRIVER = Path.of("/usr/bin/chromedriver");
    private static final Path SYSTEM_CHROMIUM = Path.of("/usr/bin/chromium");
    private static final Path SYSTEM_CHROMIUM_BROWSER = Path.of("/usr/bin/chromium-browser");

    private WebDriverFactory() {
    }

    public static void setupDriver() {
        if (Files.exists(SYSTEM_CHROMEDRIVER)) {
            System.setProperty("webdriver.chrome.driver", SYSTEM_CHROMEDRIVER.toString());
            return;
        }

        WebDriverManager.chromedriver().setup();
    }

    public static WebDriver create() {
        Configuration configuration = config();
        ChromeOptions options = new ChromeOptions();

        Optional<String> chromiumBinary = resolveChromiumBinary();
        boolean headless = configuration.headless() || chromiumBinary.isPresent();
        if (headless) {
            options.addArguments("--headless=new");
        }

        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--remote-allow-origins=*"
        );
        options.addArguments("--window-size="
                + configuration.windowWidth() + "," + configuration.windowHeight());

        chromiumBinary.ifPresent(options::setBinary);

        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().setSize(
                new Dimension(configuration.windowWidth(), configuration.windowHeight())
        );
        return driver;
    }

    private static Optional<String> resolveChromiumBinary() {
        if (Files.exists(SYSTEM_CHROMIUM)) {
            return Optional.of(SYSTEM_CHROMIUM.toString());
        }
        if (Files.exists(SYSTEM_CHROMIUM_BROWSER)) {
            return Optional.of(SYSTEM_CHROMIUM_BROWSER.toString());
        }
        return Optional.empty();
    }
}
