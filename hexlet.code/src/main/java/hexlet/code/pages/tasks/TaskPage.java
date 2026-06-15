package hexlet.code.pages.tasks;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TaskPage {

    private final WebDriver driver;
    private By assigneeFilter = By.xpath("(//*[@data-testid='ArrowDropDownIcon'])[1]");
    private By statusFilter = By.xpath("(//*[@data-testid='ArrowDropDownIcon'])[2]");
    private By labelFilter = By.xpath("(//*[@data-testid='ArrowDropDownIcon'])[3]");

    private By createButton = By.xpath("(//*[@data-testid='AddIcon'");
    private By exportButton = By.xpath("(//*[@data-testid='GetAppIcon'");


    public TaskPage(WebDriver driver) {
        this.driver = driver;
    }



}
