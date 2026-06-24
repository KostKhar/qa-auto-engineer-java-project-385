package hexlet.code.pages;

import hexlet.code.components.Header;
import hexlet.code.components.SideBar;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage {
    private static final String TITLE_OF_CONTENT_ROOT = "Lorem ipsum sic dolor amet...";
    private final By titleOfContentRootLocator = By.xpath("//*[@class='MuiCardContent-root css-15q2cw4']");
    private Header header;
    @Getter
    private SideBar sideBar;

    public DashboardPage(WebDriver driver) {
        super(driver);
        initComponents();
    }

    @Override
    public void initComponents() {
        header = new Header(driver);
        sideBar = new SideBar(driver);
    }

    public String getTitleOfContentRootByLocator() {
        return driver.findElement(titleOfContentRootLocator).getText();
    }

    public String getTitleOfContentRoot() {
        return TITLE_OF_CONTENT_ROOT;
    }

    public Header getHeader() {
        return header;
    }
}
