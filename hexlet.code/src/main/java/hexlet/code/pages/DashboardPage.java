package hexlet.code.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage {
    private final WebDriver driver;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
    }

    private static final String TITLE_OF_CONTENT_ROOT = "Lorem ipsum sic dolor amet...";
    private final By titleOfContentRootLocator = By.cssSelector("class=MuiCardContent-root css-15q2cw4");

    public String getTitleOfContentRoot() {
        return driver.findElement(titleOfContentRootLocator).getText();
    }

    public boolean titleEqualsOnDashboard() {
        return TITLE_OF_CONTENT_ROOT.equals(getTitleOfContentRoot());
    }


}
