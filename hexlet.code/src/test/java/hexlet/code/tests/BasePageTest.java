package hexlet.code.tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static hexlet.code.config.ConfigurationManager.config;


abstract class BasePageTest {
    protected WebDriver driver;

    @BeforeEach
    void startChrome() {
        driver = new ChromeDriver();
        driver.get(config().baseUrl());
    }

    @AfterEach
    void tearDownAll() {
        driver.manage().deleteAllCookies();
        driver.quit();
    }
}
