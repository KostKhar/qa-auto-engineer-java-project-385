package hexlet.code.tests;

import hexlet.code.service.EnvironmentService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


abstract class BasePageTest {
    protected static  WebDriver driver;
    private static final String APP_BASE_URL = EnvironmentService.getEnv("APP_BASE_URL");


    @BeforeAll
    static void startChrome() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(APP_BASE_URL);
    }

    @AfterAll
    static void browserQuit() {
        driver.manage().deleteAllCookies();
        driver.quit();
    }
}
