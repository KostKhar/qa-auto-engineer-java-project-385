package hexlet.code.pages.tasks;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TaskByIdForm {

    private WebDriver driver;
    public TaskByIdForm(WebDriver driver) {
        this.driver = driver;
    }

    private By title = By.className("MuiTypography-root MuiTypography-h5 css-elr2b6");
    private By description = By.className("MuiTypography-root MuiTypography-body2 css-bxmwoh");


    private By editButton = By.xpath("(//*[@data-testid='CreateIcon'");
    private By showButton = By.xpath("(//*[@data-testid='RemoveRedEyeIcon'");


}
