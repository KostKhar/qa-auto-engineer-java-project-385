package hexlet.code.pages.tasks;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TasksListPage {

    private final WebDriver driver;
    private final By assigneeFilter = By.xpath("(//*[@data-testid='ArrowDropDownIcon'])[1]");
    private final By statusFilter = By.xpath("(//*[@data-testid='ArrowDropDownIcon'])[2]");
    private final By labelFilter = By.xpath("(//*[@data-testid='ArrowDropDownIcon'])[3]");

    private final By createButton = By.xpath("//*[@data-testid='AddIcon']");
    private final By exportButton = By.xpath("//*[@data-testid='GetAppIcon']");

    public TasksListPage(WebDriver driver) {
        this.driver = driver;
    }
}
