package hexlet.code.tests;

import hexlet.code.driver.WebDriverFactory;
import hexlet.code.tests.cleanup.CleanupExtension;
import hexlet.code.tests.cleanup.WebDriverExtension;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

@ExtendWith({ WebDriverExtension.class, CleanupExtension.class })
@Getter
@Setter
public abstract class BaseTest {
    protected WebDriver driver;

    @BeforeAll
    static void setupClass() {
        WebDriverFactory.setupDriver();
    }

}
